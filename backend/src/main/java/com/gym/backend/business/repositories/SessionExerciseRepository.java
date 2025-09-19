package com.gym.backend.business.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.SessionExercise;

@Repository
public interface SessionExerciseRepository extends CrudRepository<SessionExercise, Long>{

}
