package com.gym.backend.business.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.WeeklyRoutine;

@Repository
public interface WeeklyRoutineRepository extends CrudRepository<WeeklyRoutine, Long> {

    List<WeeklyRoutine> findByUserId(Long userId);

    Optional<WeeklyRoutine> findByStartDateAndEndDate(LocalDate startDate, LocalDate endDate);
}
