package com.gym.backend.business.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.RoutineDayRepository;
import com.gym.backend.business.repositories.WeeklyRoutineRepository;
import com.gym.backend.exceptions.LimiteExcedidoException;
import com.gym.backend.model.RoutineDay;
import com.gym.backend.model.Session;
import com.gym.backend.model.User;
import com.gym.backend.model.UserPlan;
import com.gym.backend.model.WeeklyRoutine;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class WeeklyRoutineService {

    @Autowired
    private WeeklyRoutineRepository repository;

    @Autowired
    private RoutineDayRepository routineDayRepository;

    @Autowired
    private RoutineDayService routineDayService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserPlanService userPlanService;

    private final ZoneId zoneId;

    public WeeklyRoutineService(ZoneId zoneId) {
        this.zoneId = zoneId; // Spring inyecta el bean
    }

    public WeeklyRoutine findById(long id) {
        return repository.findById(id).orElse(null);
    }

    public List<WeeklyRoutine> findAll() {
        List<WeeklyRoutine> result = new ArrayList<>();
        repository.findAll().forEach(result::add);
        return result;
    }

    // 🔹 Buscar rutina por fechas (para un usuario)
    public WeeklyRoutine findByDatesAndUserId(LocalDate start, LocalDate end, Long userId) {
        return repository.findByStartDateAndEndDateAndUserId(start, end, userId)
                .orElse(null);
    }

    public List<WeeklyRoutine> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    private void validarRutina(WeeklyRoutine weeklyRoutine) {
        LocalDate start = weeklyRoutine.getStartDate();
        LocalDate end = weeklyRoutine.getEndDate();

        boolean esActualizacion = weeklyRoutine.getId() != null;

        if (esActualizacion) {
            WeeklyRoutine existente = repository.findById(weeklyRoutine.getId())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró la rutina semanal."));

            // 🔹Si la rutina semanal ya finalizó, no se puede modificar nada
            if (existente.getEndDate() != null && LocalDate.now(zoneId).isAfter(existente.getEndDate())) {
                throw new IllegalArgumentException("No se puede modificar una rutina semanal que ya ha finalizado.");
            }

            // 🔹Si tiene rutinas diarias asociadas, solo se bloquea la edición de fechas
            long count = routineDayRepository.countByRoutineId(weeklyRoutine.getId());
            if (count > 0) {
                boolean fechasCambiaron = (start != null && !start.equals(existente.getStartDate())) ||
                        (end != null && !end.equals(existente.getEndDate()));

                if (fechasCambiaron) {
                    throw new IllegalArgumentException(
                            "No se pueden modificar las fechas de una rutina semanal con rutinas diarias asociadas.");
                }
            }
        }

        if ((start == null && end != null) || (start != null && end == null))
            throw new IllegalStateException("Ambas fechas deben estar definidas o ninguna.");

        if (start != null && end != null) {

            if (start.isAfter(end))
                throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");

            if (start.getDayOfWeek() != DayOfWeek.MONDAY)
                throw new IllegalArgumentException("La rutina semanal debe comenzar un lunes.");

            long dias = ChronoUnit.DAYS.between(start, end) + 1; // +1 para incluir ambos extremos
            if (dias < 5 || dias > 7)
                throw new IllegalArgumentException("La rutina semanal debe tener entre 5 y 7 días.");

            List<WeeklyRoutine> solap;
            if (weeklyRoutine.getId() != null) {
                // Si es actualización, excluir la rutina actual
                solap = repository.findOverlappingExcludingId(start, end, weeklyRoutine.getId(),
                        weeklyRoutine.getUser().getId());
            } else {
                // Si es creación, revisar solapamientos normales
                solap = repository.findOverlapping(start, end, weeklyRoutine.getUser().getId());
            }

            if (!solap.isEmpty())
                throw new IllegalArgumentException("La rutina semanal se solapa con otra existente.");
        }
    }

    // Verifica si el usuario ha alcanzado el límite de rutinas semanales según su plan
    private boolean limiteAlcanzado(){
        User user = userService.getAuthenticatedUser();
        UserPlan userPlan = userPlanService.getActivePlan(user.getId());
        return repository.findByUserId(user.getId()).size() >= userPlan.getPlan().getType().getMaxRutinasSemanales();
    }

    @Transactional
    public WeeklyRoutine crear(WeeklyRoutine weeklyRoutine) {
        if(limiteAlcanzado()){ // Valida que no se haya alcanzado el límite de rutinas semanales
            throw new LimiteExcedidoException("El límite de rutinas semanales para tu plan ha sido alcanzado. Ya tenés "
                + repository.findByUserId(userService.getAuthenticatedUser().getId()).size() + " rutinas.");
        }
        validarRutina(weeklyRoutine);
        return repository.save(weeklyRoutine);
    }

    @Transactional
    public WeeklyRoutine actualizar(WeeklyRoutine weeklyRoutine) {
        validarRutina(weeklyRoutine);
        return repository.save(weeklyRoutine);
    }

    @Transactional
    public void delete(long weeklyRoutineId) {
        long count = routineDayRepository.countByRoutineId(weeklyRoutineId);
        if (count > 0) {
            throw new IllegalArgumentException(
                    "No se puede eliminar esta rutina semanal, porque tiene asociadas rutinas diarias.");
        }

        repository.deleteById(weeklyRoutineId);
    }

    @Transactional
    public WeeklyRoutine clone(WeeklyRoutine weeklyRoutine, LocalDate startDate) {

        if(limiteAlcanzado()){ // Valida que no se haya alcanzado el límite de rutinas semanales
            throw new LimiteExcedidoException("El límite de rutinas semanales para tu plan ha sido alcanzado. Ya tenés "
                + repository.findByUserId(userService.getAuthenticatedUser().getId()).size() + " rutinas.");
        }

        if (weeklyRoutine.getStartDate() == null || weeklyRoutine.getEndDate() == null) {
            throw new IllegalArgumentException(
                    "La rutina semanal original debe tener fechas definidas para ser clonada.");
        }

        WeeklyRoutine clonada = new WeeklyRoutine();

        clonada.setName(weeklyRoutine.getName()); // mismo nombre
        clonada.setDescription(weeklyRoutine.getDescription()); // misma descripción
        clonada.setStartDate(startDate); // nueva fecha, ingresada en el método

        // Mantener la misma duración en días que la original
        long dias = ChronoUnit.DAYS.between(weeklyRoutine.getStartDate(), weeklyRoutine.getEndDate());
        clonada.setEndDate(startDate.plusDays(dias));
        clonada.setUser(weeklyRoutine.getUser()); // mismo usuario

        clonada = crear(clonada);

        List<RoutineDay> diasAsociados = routineDayRepository.findByWeeklyRoutine(weeklyRoutine);

        // Evitar clonar la misma sesión más de una vez
        Map<Long, Session> sesionesClonadas = new HashMap<>();

        for (RoutineDay rd : diasAsociados) {
            RoutineDay nuevo = new RoutineDay();
            nuevo.setDay(rd.getDay());
            nuevo.setRoutine(clonada);

            Session sesionBase = rd.getSession();
            Session sesionClonada = sesionesClonadas.get(sesionBase.getId());

            if (sesionClonada == null) {
                sesionClonada = sessionService.clone(sesionBase, clonada.getUser());
                sesionesClonadas.put(sesionBase.getId(), sesionClonada);
            }

            nuevo.setSession(sesionClonada);

            // No seteamos status manualmente, lo hará routineDayService.save()
            routineDayService.update(nuevo);
        }

        return clonada;
    }

}
