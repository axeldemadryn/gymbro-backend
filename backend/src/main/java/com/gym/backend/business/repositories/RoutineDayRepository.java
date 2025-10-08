package com.gym.backend.business.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.RoutineDay;

@Repository
public interface RoutineDayRepository extends CrudRepository<RoutineDay, Long> {

    long countBySessionId(Long sessionId);

}
