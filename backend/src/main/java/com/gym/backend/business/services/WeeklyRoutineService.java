package com.gym.backend.business.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.WeeklyRoutineRepository;
import com.gym.backend.model.WeeklyRoutine;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class WeeklyRoutineService {

    @Autowired
    private WeeklyRoutineRepository repository;

    public WeeklyRoutine findById(long id) {
        return repository.findById(id).orElse(null);
    }

    public List<WeeklyRoutine> findAll() {
        List<WeeklyRoutine> result = new ArrayList<>();
        repository.findAll().forEach(result::add);
        return result;
    }

    public List<WeeklyRoutine> findByUserId(Long userId) {
        if (userId == null) {
            return findAll(); // Si no hay usuarios, devuelve todas
        }
        return repository.findByUserId(userId);
    }

    @Transactional
    public WeeklyRoutine save(WeeklyRoutine weeklyRoutine) {
        LocalDate start = weeklyRoutine.getStartDate();
        LocalDate end = weeklyRoutine.getEndDate();

        if (start != null && end != null) {

            if (start.isAfter(end))
                throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");

            long dias = ChronoUnit.DAYS.between(start, end) + 1; // +1 para incluir ambos extremos
            if (dias < 5 || dias > 7)
                throw new IllegalArgumentException("La rutina semanal debe tener entre 5 y 7 días.");
        }
        return repository.save(weeklyRoutine);
    }

    @Transactional
    public void delete(long weeklyRoutineId) {
        repository.deleteById(weeklyRoutineId);
    }
}
