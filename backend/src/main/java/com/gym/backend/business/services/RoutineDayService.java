package com.gym.backend.business.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.RoutineDayRepository;
import com.gym.backend.business.repositories.SessionExerciseRepository;
import com.gym.backend.business.repositories.WeeklyRoutineRepository;
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
        WeeklyRoutine rutina = rd.getRoutine();
        LocalDate start = rutina.getStartDate();
        LocalDate end = rutina.getEndDate();

        if (start == null && end == null)
            throw new IllegalStateException(
                    "No se puede crear la rutina diaria: la rutina semanal aún no tiene fechas definidas.");

        // Validación: el día de la semana debe caer dentro del rango de fechas
        boolean diaValido = start.datesUntil(end.plusDays(1)) // genera un Stream de LocalDate
                .anyMatch(fecha -> fecha.getDayOfWeek().getValue() == rd.getDay().getDia().getValue());

        if (!diaValido) {
            throw new IllegalArgumentException(
                    "El día seleccionado no cae dentro del rango de fechas de la rutina semanal.");
        }

        // Validar que la sesión tenga al menos un ejercicio
        long cantidadEjercicios = sessionExerciseRepository.countBySessionId(rd.getSession().getId());
        if (cantidadEjercicios == 0) {
            throw new IllegalArgumentException("La sesión debe tener al menos un ejercicio.");
        }

        // Evaluar si la sesión pendiente ya pasó
        if (rd.getStatus() == SessionStatus.PENDIENTE
                && LocalDate.now().isAfter(start)
                && LocalDate.now().getDayOfWeek().getValue() > rd.getDay().getDia().getValue()) {
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
            WeeklyRoutine routine = weeklyRoutineRepository.findById(routineDay.getRoutine().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Se intentó conseguir la rutina a partir del ID, pero no se pudo."));
            routineDay.setRoutine(routine);
        }
        evaluarRutina(routineDay); // Setea el estado a no completada, si corresponde; y controla si el día está
                                   // dentro del rango de fechas
        return repository.save(routineDay);
    }

    @Transactional
    public RoutineDay marcarCompletada(RoutineDay routine) {
        // Agregar control de que si hoy no es el dia de la rutina diaria, aunque
        // esté Pendiente, no se pueda marcar como Completada
        if (routine.getStatus().equals(SessionStatus.PENDIENTE)) {
            routine.setStatus(SessionStatus.COMPLETADA);
        } else {
            throw new RuntimeException(routine.getStatus().equals(SessionStatus.COMPLETADA)
                    ? "La rutina ya está completada." // no se puede marcar como completada una rutina ya completada y
                                                      // marcada
                    : "Error. La rutina ya ha expirado."); // rutina incompleta
        }
        return repository.save(routine);
    }

    @Transactional
    public void delete(long routineDayId) {
        repository.deleteById(routineDayId);
    }
}