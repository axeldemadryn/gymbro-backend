package com.gym.backend.business.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.dto.EstadisticaSemanalDTO;
import com.gym.backend.model.Ejercicio;
import com.gym.backend.model.Musculo;
import com.gym.backend.model.RoutineDay;
import com.gym.backend.model.Session;
import com.gym.backend.model.SessionExercise;
import com.gym.backend.model.SessionStatus;
import com.gym.backend.model.WeeklyRoutine;
import com.gym.backend.business.repositories.RoutineDayRepository;

@Service
public class EstadisticaService {

    @Autowired
    private RoutineDayRepository routineDayRepository;

    public EstadisticaSemanalDTO calcular(WeeklyRoutine routine) {

        EstadisticaSemanalDTO dto = new EstadisticaSemanalDTO();

        List<RoutineDay> dias = routineDayRepository.findByRoutineId(routine.getId());

        int totalDiasConSesion = dias.size();
        dto.setTotalDiasConSesiones(totalDiasConSesion);

        int completadas = (int) dias.stream()
                .filter(d -> d.getStatus() == SessionStatus.COMPLETADA)
                .count();

        dto.setSesionesCompletadas(completadas);
        dto.setSesionesPendientes(totalDiasConSesion - completadas);

        // Ejercicios planificados y completados
        int ejerciciosPlanificados = 0;
        int ejerciciosCompletados = 0;

        Map<String, Integer> musculos = new HashMap<>();

        for (RoutineDay d : dias) {

            Session s = d.getSession();
            if (s == null)
                continue; // seguridad adicional

            if (s.getSessionExercises() == null)
                continue;

            List<SessionExercise> sesEx = s.getSessionExercises()
                    .stream()
                    .toList();

            ejerciciosPlanificados += sesEx.size();

            if (d.getStatus() == SessionStatus.COMPLETADA) {
                ejerciciosCompletados += sesEx.size();

                for (SessionExercise se : sesEx) {
                    Ejercicio ej = se.getExercise();
                    if (ej == null || ej.getMusculos() == null)
                        continue;

                    for (Musculo m : ej.getMusculos()) {
                        musculos.merge(m.getNombre(), 1, Integer::sum);
                    }
                }
            }
        }

        dto.setTotalEjerciciosPlanificados(ejerciciosPlanificados);
        dto.setEjerciciosCompletados(ejerciciosCompletados);
        dto.setMusculosMasTrabajados(musculos);

        dto.setPorcentajeAdherencia(
                totalDiasConSesion == 0 ? 0 : (completadas * 100.0) / totalDiasConSesion);

        dto.setPorcentajeEjerciciosCompletados(
                ejerciciosPlanificados == 0 ? 0 : (ejerciciosCompletados * 100.0) / ejerciciosPlanificados);

        return dto;
    }
}
