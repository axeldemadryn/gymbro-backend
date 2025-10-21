package com.gym.backend.business.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    // Buscar un usuario por su email (para login o validaciones)
    Optional<User> findByEmail(String email);

    // Verificar si ya existe un email registrado (para evitar duplicados)
    boolean existsByEmail(String email);
}
