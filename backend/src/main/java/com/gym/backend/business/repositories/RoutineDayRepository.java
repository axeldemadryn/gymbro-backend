package com.gym.backend.business.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.DiaDeSemana;
import com.gym.backend.model.RoutineDay;
import com.gym.backend.model.WeeklyRoutine;

@Repository
public interface RoutineDayRepository extends CrudRepository<RoutineDay, Long> {

    long countBySessionId(Long sessionId);

    long countByRoutineId(Long routineid);

    @Query("SELECT r FROM RoutineDay r WHERE r.day = ?1 AND r.routine = ?2")
    Optional<RoutineDay> findByDayAndWeeklyRoutine(DiaDeSemana day, WeeklyRoutine routine);

}
