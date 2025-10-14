package com.gym.backend.business.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.RoutineDayRepository;
import com.gym.backend.business.repositories.SessionExerciseRepository;
import com.gym.backend.business.repositories.WeeklyRoutineRepository;
import com.gym.backend.model.DiaDeSemana;
import com.gym.backend.model.RoutineDay;
import com.gym.backend.model.SessionStatus;
import com.gym.backend.model.WeeklyRoutine;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RoutineDayService {
    @Autowired
    private RoutineDayRepository repository; // Repositorio de esta entidad

    @Autowired
    private WeeklyRoutineRepository weeklyRoutineRepository;

    @Autowired
    private SessionExerciseRepository sessionExerciseRepository;

    private void evaluarRutina(RoutineDay rd) {
        // Las fechas de rutina semanal no pueden ser ambas nulas
        if (rd.getRoutine().getStartDate() == null && rd.getRoutine().getEndDate() == null)
            throw new IllegalStateException(
                    "No se puede crear la rutina diaria: la rutina semanal aún no tiene fechas definidas.");

        /* El día de la semana debe caer dentro del rango de fechas (NOTA: el datesUntil genera un Stream de
        LocalDate)*/
        if (rd.getRoutine().getStartDate().datesUntil(rd.getRoutine().getEndDate().plusDays(1))
                .noneMatch(fecha -> fecha.getDayOfWeek().getValue() == rd.getDay().getDia().getValue())) {
            throw new IllegalArgumentException(
                    "El día seleccionado no cae dentro del rango de fechas de la rutina semanal.");
        }

        /* Validar que la sesión tenga al menos un ejercicio (el lado izquierdo de la igualdad cuenta la cantidad
        de ejercicios asociados a la sesión de la rutina diaria)*/
        if (sessionExerciseRepository.countBySessionId(rd.getSession().getId()) == 0) {
            throw new IllegalArgumentException("La sesión debe tener al menos un ejercicio.");
        }

        /* Si la rutina era pendiente, y si además la semana completa ya pasó o bien la semana ya inició y ya pasó
        el día de semana de la propia rutina diaria, entonces se marca como no completada */
        if (rd.getStatus() == SessionStatus.PENDIENTE && // La rutina debe ser pendiente para aplicar el IF.
                (LocalDate.now().isAfter(rd.getRoutine().getEndDate()) || // La rutina semanal completa debe haber pasado, o bien...
                    (
                        // Al menos, la rutina semanal debería haber empezado, y el día de rutina haber transcurrido.
                        LocalDate.now().isAfter(rd.getRoutine().getStartDate()) && 
                        LocalDate.now().getDayOfWeek().getValue() > rd.getDay().getDia().getValue()
                    )
                )
            ) {
            rd.setStatus(SessionStatus.NO_COMPLETADA);
        }
    }

    @Transactional
    public void actualizarEstadosSegunHoy() {
        List<RoutineDay> routineDays = logicaObtencionDeTodas();

        for (RoutineDay rd : routineDays) {
            evaluarRutina(rd);
        }
        repository.saveAll(routineDays);
    }

    public RoutineDay findById(long id) {
        actualizarEstadosSegunHoy();
        return repository.findById(id).orElse(null);
    }

    public RoutineDay findByDayAndWeeklyRoutineDates(DiaDeSemana day, LocalDate start, LocalDate end){
        WeeklyRoutine routine = weeklyRoutineRepository.findByStartDateAndEndDate(start, end).orElseThrow(() -> new RuntimeException("No se encontró una rutina semanal con esas fechas"));
        return repository.findByDayAndWeeklyRoutine(day, routine).orElse(null);
    }

    private List<RoutineDay> logicaObtencionDeTodas() {
        List<RoutineDay> result = new ArrayList<>();
        repository.findAll().forEach(result::add);
        return result;
    }

    public List<RoutineDay> findAll() {
        actualizarEstadosSegunHoy();
        return logicaObtencionDeTodas();
    }

    @Transactional
    public RoutineDay save(RoutineDay routineDay) {
        routineDay.setStatus(SessionStatus.PENDIENTE); // Pasa el estado a pendiente
        if (routineDay.getRoutine().getName() == null) {
            routineDay.setRoutine(
                weeklyRoutineRepository.findById(routineDay.getRoutine().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Se intentó conseguir la rutina a partir del ID, pero no se pudo."))
            );
        }
        evaluarRutina(routineDay);

        return repository.save(routineDay);
    }

    @Transactional
    public RoutineDay marcarCompletada(RoutineDay routine) {
        // Validar que el estado sea PENDIENTE
        if(!routine.getStatus().equals(SessionStatus.PENDIENTE))
            throw new IllegalStateException(routine.getStatus().equals(SessionStatus.COMPLETADA)
                    ? "La rutina ya está completada." // no se puede marcar como completada una rutina ya completada y
                                                      // marcada
                    : "Error. La rutina ya ha expirado."); // rutina incompleta

        // Validar que hoy es el día correspondiente
        if (!LocalDate.now()
            // Calcular el LocalDate real del RoutineDay según el día de la semana (dentro de isEqual)
            .isEqual(routine.getRoutine().getStartDate()
                /* plusDays añade a la fecha (LocalDate) de inicio de la rutina semanal una cantidad de días dada
                para devolver otra fecha en consecuencia; y getValue devuelve el número de día del 1 al 7
                (LUNES=1 ... DOMINGO=7) */
                .plusDays((long) routine.getDay().getDia().getValue() - 1)
            )
        )
            throw new RuntimeException("Error. Sólo se puede marcar como completada si el día de hoy corresponde al de la rutina.");

        // Cambia de estado y guarda
        routine.setStatus(SessionStatus.COMPLETADA);
        return repository.save(routine);
    }

    @Transactional
    public void delete(long routineDayId) {
        repository.deleteById(routineDayId);
    }
}