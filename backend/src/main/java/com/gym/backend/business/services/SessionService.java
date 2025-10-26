package com.gym.backend.business.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.MaquinaRepository;
import com.gym.backend.business.repositories.RoutineDayRepository;
import com.gym.backend.business.repositories.SessionRepository;
import com.gym.backend.dto.EjercicioDTO;
import com.gym.backend.dto.MaquinaDTO;
import com.gym.backend.dto.MusculoDTO;
import com.gym.backend.model.Maquina;
import com.gym.backend.model.Session;
import com.gym.backend.model.SessionExercise;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SessionService {
    @Autowired
    private SessionRepository repository;

    @Autowired
    private RoutineDayRepository routineDayRepository;

    @Autowired
    private MaquinaRepository maquinaRepository;

    public Session findById(long id) {
        return repository.findById(id).orElse(null);
    }

    public Session findByName(String name) {
        return repository.findByName(name).orElse(null);
    }

    public List<Session> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    public Session findByNameAndUserId(String name, Long userId) {
        return repository.findByNameAndUserId(name, userId).orElse(null);
    }

    public List<Session> findAll() {
        List<Session> result = new ArrayList<>();
        repository.findAll().forEach(aSession -> result.add(aSession));
        return result;
    }

    @Transactional
    public Session save(Session aSession) {

        /* Si es actualización (el ID no es nulo), se verifica si la sesión está asociada a
        alguna rutina diaria */
        if (aSession.getId() != null && routineDayRepository.countBySessionId(aSession.getId()) > 0) {
            throw new IllegalArgumentException(
                    "No se puede modificar esta sesión, porque está asociada a una rutina diaria.");
        }

        // Setear la relación bidireccional
        if (aSession.getSessionExercises() != null) {
            for (SessionExercise se : aSession.getSessionExercises()) {
                se.setSession(aSession);
            }
        }
        return repository.save(aSession);
    }

    @Transactional
    public void delete(long sessionId) {
        long count = routineDayRepository.countBySessionId(sessionId);
        if (count > 0) {
            throw new IllegalArgumentException(
                    "No se puede eliminar esta sesión, porque está asociada a rutinas diarias.");
        }

        repository.deleteById(sessionId);
    }

    public List<MaquinaDTO> obtenerMaquinasPorIdDeSesion(Long sessionId) {
        List<Maquina> maquinas = maquinaRepository.findMaquinasBySessionId(sessionId);

        // Mapear cada Maquina a MaquinaDTO, incluyendo la lista de MusculoDTO y, opcionalmente, EjercicioDTO
        return maquinas.stream().map(m -> {
            MaquinaDTO dto = new MaquinaDTO();
            dto.setNombre(m.getNombre());
            dto.setTipoEquipo(m.getTipoEquipo() != null ? m.getTipoEquipo().name() : null);
            dto.setDescripcion(m.getDescripcion());
            dto.setImagen(m.getImagenUrl());

            // Musculos (requerido)
            if (m.getMusculos() != null) {
                dto.setMusculos(m.getMusculos().stream()
                    .map(mu -> new MusculoDTO(mu.getNombre()))
                    .toList());
            }

            // Ejercicios (opcional, pero añadimos la información básica si está cargada)
            if (m.getEjercicios() != null) {
                dto.setEjercicios(m.getEjercicios().stream().map(e -> {
                    EjercicioDTO ed = new EjercicioDTO();
                    ed.setNombre(e.getNombre());
                    ed.setTipo(e.getTipo() != null ? e.getTipo().name() : null);
                    ed.setDescripcion(e.getDescripcion());
                    ed.setVideoUrl(e.getVideoUrl());
                    // musculosPrincipales en el DTO de ejercicio: mapeamos si existen
                    if (e.getMusculos() != null) {
                        ed.setMusculosPrincipales(e.getMusculos().stream()
                            .map(mu -> new MusculoDTO(mu.getNombre()))
                            .toList());
                    }
                    return ed;
                }).toList());
            }

            return dto;
        }).toList();
    }
}
