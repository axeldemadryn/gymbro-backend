package com.gym.backend.business.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.SessionExerciseRepository;
import com.gym.backend.model.SessionExercise;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SessionExerciseService {
    @Autowired
    private SessionExerciseRepository repository;

    public SessionExercise findById(long id){
        return repository.findById(id).orElse(null);
    }

    public List<SessionExercise> findAll(){
        List<SessionExercise> result = new ArrayList<>();
        repository.findAll().forEach(sessionExercise -> result.add(sessionExercise));
        return result;
    }

    @Transactional
    public SessionExercise save(SessionExercise e){
        return repository.save(e);
    }

    @Transactional
    public void delete(long anId){
        repository.deleteById(anId);
    }
}
