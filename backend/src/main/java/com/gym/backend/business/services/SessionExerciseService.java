package com.gym.backend.business.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.RoutineDayRepository;
import com.gym.backend.business.repositories.SessionExerciseRepository;
import com.gym.backend.model.RoutineDay;
import com.gym.backend.model.SessionExercise;
import com.gym.backend.model.SessionStatus;
import com.gym.backend.model.WeeklyRoutine;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SessionExerciseService {
    @Autowired
    private SessionExerciseRepository repository;

    @Autowired
    private RoutineDayRepository routineDayRepository;

    private final ZoneId zoneId;

    public SessionExerciseService(ZoneId zoneId) {
        this.zoneId = zoneId; // Spring inyecta el bean
    }

    // 🔹 Nuevo método: obtener todos los ejercicios de sesión de un usuario
    public List<SessionExercise> findAllByUserId(Long userId) {
        return repository.findAllBySessionUserId(userId);
    }

    public SessionExercise findById(long id) {
        return repository.findById(id).orElse(null);
    }

    public List<SessionExercise> findAll() {
        List<SessionExercise> result = new ArrayList<>();
        repository.findAll().forEach(sessionExercise -> result.add(sessionExercise));
        return result;
    }

    @Transactional
    public SessionExercise save(SessionExercise e) {

        // Validación de sets y reps mínimos
        if (e.getSets() < 1) {
            throw new IllegalStateException("El número de series debe ser al menos 1.");
        }

        if (e.getReps() < 3) {
            throw new IllegalStateException("El número de repeticiones debe ser al menos 3.");
        }

        if (e.getSession() != null && e.getSession().getId() != null) {
            List<RoutineDay> diasAsociados = routineDayRepository.findBySessionId(e.getSession().getId());

            if (!diasAsociados.isEmpty()) {
                // Verificar si la sesión pertenece a una rutina ya pasada (Completada o No
                // Completada)
                boolean rutinaPasada = diasAsociados.stream()
                        .anyMatch(rd -> {
                            WeeklyRoutine weeklyRoutine = rd.getRoutine(); // se obtiene la rutina semanal

                            // Fecha real del RoutineDay (asumiendo que la semana empieza lunes)
                            LocalDate fechaDia = weeklyRoutine.getStartDate()
                                    .plusDays(rd.getDay().getDia().getValue() - 1);

                            // Bloqueamos si ya pasó la fecha o el estado indica que se completó/no se
                            // completó
                            return fechaDia.isBefore(LocalDate.now(zoneId)) ||
                                    rd.getStatus() == SessionStatus.COMPLETADA ||
                                    rd.getStatus() == SessionStatus.NO_COMPLETADA;
                        });

                if (rutinaPasada) {
                    throw new IllegalStateException(
                            "No se pueden crear o modificar ejercicios de una sesión que pertenece a una rutina ya pasada.");
                }
            }
        }

        return repository.save(e);
    }

    @Transactional
    public void delete(long anId) {
        SessionExercise e = findById(anId);

        if (e.getSession() != null && e.getSession().getId() != null) {
            List<RoutineDay> diasAsociados = routineDayRepository.findBySessionId(e.getSession().getId());

            if (!diasAsociados.isEmpty()) {
                // Verificar si la sesión pertenece a una rutina ya pasada (Completada o No
                // Completada)
                boolean rutinaPasada = diasAsociados.stream()
                        .anyMatch(rd -> {
                            WeeklyRoutine weeklyRoutine = rd.getRoutine(); // se obtiene la rutina semanal

                            // Fecha real del RoutineDay (asumiendo que la semana empieza lunes)
                            LocalDate fechaDia = weeklyRoutine.getStartDate()
                                    .plusDays(rd.getDay().getDia().getValue() - 1);

                            // Bloqueamos si ya pasó la fecha o el estado indica que se completó/no se
                            // completó
                            return fechaDia.isBefore(LocalDate.now(zoneId)) ||
                                    rd.getStatus() == SessionStatus.COMPLETADA ||
                                    rd.getStatus() == SessionStatus.NO_COMPLETADA;
                        });

                if (rutinaPasada) {
                    throw new IllegalStateException(
                            "No se puede eliminar un ejercicio de una sesión que pertenece a una rutina ya pasada.");
                }
            }
        }

        repository.deleteById(anId);
    }
}
