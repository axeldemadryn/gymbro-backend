package com.gym.backend.business.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.WeeklyRoutine;

@Repository
public interface WeeklyRoutineRepository extends CrudRepository<WeeklyRoutine, Long> {
    List<WeeklyRoutine> findByUserId(Long userId);
}
