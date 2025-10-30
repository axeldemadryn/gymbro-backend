package com.gym.backend.business.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.Session;

@Repository
public interface SessionRepository extends CrudRepository<Session, Long> {

    Optional<Session> findByName(String nombre);

    // Todas las sesiones de un usuario
    List<Session> findByUserId(Long userId);

    // Buscar por nombre dentro de un usuario
    Optional<Session> findByNameAndUserId(String name, Long userId);

    boolean existsByNameAndUserId(String name, Long userId);

}
