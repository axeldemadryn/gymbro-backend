package com.gym.backend.business.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.RoutineDayRepository;
import com.gym.backend.model.RoutineDay;

import jakarta.transaction.Transactional;

@Service
public class RoutineDayService {
    @Autowired
    private RoutineDayRepository repository;

    public RoutineDay findById(long id) {
        return repository.findById(id).orElse(null);
    }

    public List<RoutineDay> findAll() {
        List<RoutineDay> result = new ArrayList<>();
        repository.findAll().forEach(result::add);
        return result;
    }

    @Transactional
    public RoutineDay save(RoutineDay routineDay) {
        return repository.save(routineDay);
    }

    @Transactional
    public void delete(long routineDayId) {
        repository.deleteById(routineDayId);
    }
}
