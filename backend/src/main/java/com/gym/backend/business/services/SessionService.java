package com.gym.backend.business.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.MaquinaRepository;
import com.gym.backend.business.repositories.RoutineDayRepository;
import com.gym.backend.business.repositories.SessionRepository;
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

    public List<Maquina> obtenerMaquinasPorIdDeSesion(Long sessionId) {
        return maquinaRepository.findMaquinasBySessionId(sessionId);
    }
}
