package com.gym.backend.business.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.gym.backend.model.Plan;
import com.gym.backend.model.TipoPlan;

public interface PlanRepository extends CrudRepository<Plan, Long> {

    Optional<Plan> findByType(TipoPlan type);
}
