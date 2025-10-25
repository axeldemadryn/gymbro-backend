package com.gym.backend.business.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.RoutineDayRepository;
import com.gym.backend.business.repositories.WeeklyRoutineRepository;
import com.gym.backend.model.RoutineDay;
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

    public WeeklyRoutine findById(long id) {
        return repository.findById(id).orElse(null);
    }

    public List<WeeklyRoutine> findAll() {
        List<WeeklyRoutine> result = new ArrayList<>();
        repository.findAll().forEach(result::add);
        return result;
    }

    public WeeklyRoutine findByDates(LocalDate start, LocalDate end) {
        return repository.findByStartDateAndEndDate(start, end)
                .orElseThrow(() -> new RuntimeException("No se encontró la rutina semanal con esas fechas"));
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

        // Caso actualización: verificar si la rutina semanal está asociada a alguna
        // rutina
        // diaria
        if (esActualizacion) {
            long count = routineDayRepository.countByRoutineId(weeklyRoutine.getId());
            if (count > 0) {
                throw new IllegalArgumentException(
                        "No se puede modificar esta rutina semanal, porque está asociada a una rutina diaria.");
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
                solap = repository.findOverlappingExcludingId(start, end, weeklyRoutine.getId());
            } else {
                // Si es creación, revisar solapamientos normales
                solap = repository.findOverlapping(start, end);
            }

            if (!solap.isEmpty())
                throw new IllegalArgumentException("La rutina semanal se solapa con otra existente.");
        }
    }

    @Transactional
    public WeeklyRoutine save(WeeklyRoutine weeklyRoutine) {
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

        WeeklyRoutine clonada = new WeeklyRoutine();

        clonada.setName(weeklyRoutine.getName()); // mismo nombre
        clonada.setDescription(weeklyRoutine.getDescription()); // misma descripción
        clonada.setStartDate(startDate); // nueva fecha, ingresada en el método

        // Mantener la misma duración en días que la original
        long dias = ChronoUnit.DAYS.between(weeklyRoutine.getStartDate(), weeklyRoutine.getEndDate());
        clonada.setEndDate(startDate.plusDays(dias));
        clonada.setUser(weeklyRoutine.getUser()); // mismo usuario

        clonada = save(clonada);

        for (RoutineDay rd : routineDayRepository.findByWeeklyRoutine(weeklyRoutine)) {
            RoutineDay nuevo = new RoutineDay();
            nuevo.setDay(rd.getDay());
            nuevo.setRoutine(clonada);
            nuevo.setSession(rd.getSession());

            // No seteamos status manualmente, lo hará routineDayService.save()
            routineDayService.save(nuevo);
        }

        return clonada;
    }
}
