package com.gym.backend.business.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.gym.backend.model.UserPlan;

public interface UserPlanRepository extends CrudRepository<UserPlan, Long> {

    // obtener el plan activo de un usuario
    Optional<UserPlan> findByUserIdAndCanceledFalse(Long userId);

    // historial completo (si lo necesitás)
    List<UserPlan> findByUserIdOrderByStartDateDesc(Long userId);

    boolean existsByUserIdAndCanceledFalse(Long id);

    boolean existsByUserId(Long id);

    List<UserPlan> findAllByCanceledFalse();

}
