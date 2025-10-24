package com.gym.backend.business.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.WeeklyRoutine;

@Repository
public interface WeeklyRoutineRepository extends CrudRepository<WeeklyRoutine, Long> {

        List<WeeklyRoutine> findByUserId(Long userId);

        Optional<WeeklyRoutine> findByStartDateAndEndDate(LocalDate startDate, LocalDate endDate);

        Optional<WeeklyRoutine> findByStartDateAndEndDateAndUserId(LocalDate startDate, LocalDate endDate, Long userId);

        // Validar solapamiento de fechas
        @Query("SELECT w FROM WeeklyRoutine w " +
                        "WHERE (:startDate <= w.endDate) AND (:endDate >= w.startDate)")
        List<WeeklyRoutine> findOverlapping(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT w FROM WeeklyRoutine w " +
                        "WHERE (:startDate <= w.endDate) AND (:endDate >= w.startDate) " +
                        "AND (:id IS NULL OR w.id <> :id)")
        List<WeeklyRoutine> findOverlappingExcludingId(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("id") Long id);
}
