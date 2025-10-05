package com.gym.backend.business.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.Session;

@Repository
public interface SessionRepository extends CrudRepository<Session, Long>{

}
