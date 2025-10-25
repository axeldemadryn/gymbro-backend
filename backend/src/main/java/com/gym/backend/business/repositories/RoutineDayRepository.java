package com.gym.backend.business.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.DiaDeSemana;
import com.gym.backend.model.RoutineDay;
import com.gym.backend.model.WeeklyRoutine;

@Repository
public interface RoutineDayRepository extends CrudRepository<RoutineDay, Long> {

    long countBySessionId(Long sessionId);

    long countByRoutineId(Long routineid);

    // Buscar todos los RoutineDay de un usuario por userId
    @Query("SELECT rd FROM RoutineDay rd WHERE rd.routine.user.id = :userId")
    List<RoutineDay> findAllByRoutineUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM RoutineDay r WHERE r.day = ?1 AND r.routine = ?2")
    Optional<RoutineDay> findByDayAndWeeklyRoutine(DiaDeSemana day, WeeklyRoutine routine);

    // Buscar todas las rutinas diarias de una rutina semanal
    @Query("SELECT r FROM RoutineDay r WHERE r.routine = ?1")
    List<RoutineDay> findByWeeklyRoutine(WeeklyRoutine routine);
}
