package com.gym.backend.business.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.RoutineDayRepository;
import com.gym.backend.business.repositories.SessionExerciseRepository;
import com.gym.backend.model.SessionExercise;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SessionExerciseService {
    @Autowired
    private SessionExerciseRepository repository;

    @Autowired
    private RoutineDayRepository routineDayRepository;

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
        if (e.getSession() != null && e.getSession().getId() != null) {
            long cantidad = routineDayRepository.countBySessionId(e.getSession().getId());
            if (cantidad > 0) {
                throw new IllegalStateException(
                        "No se pueden modificar los ejercicios de una sesión ya asignada a una rutina diaria.");
            }
        }

        return repository.save(e);
    }

    @Transactional
    public void delete(long anId) {
        SessionExercise e = findById(anId);

        if (e.getSession() != null && e.getSession().getId() != null) {
            long cantidad = routineDayRepository.countBySessionId(e.getSession().getId());
            if (cantidad > 0) {
                throw new IllegalStateException(
                        "No se puede eliminar el ejercicio de una sesión ya asignada a una rutina diaria.");
            }
        }

        repository.deleteById(anId);
    }
}
