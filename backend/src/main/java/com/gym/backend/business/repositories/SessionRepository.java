package com.gym.backend.business.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.Session;

@Repository
public interface SessionRepository extends CrudRepository<Session, Long> {

    Optional<Session> findByName(String nombre);

}
