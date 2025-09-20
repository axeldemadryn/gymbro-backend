package com.gym.backend.business.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.Musculo;

@Repository
public interface MusculoRepository extends CrudRepository<Musculo, Long> {
    Optional<Musculo> findByNombreIgnoreCase(String nombre);
}
