package com.gym.backend.business.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.Maquina;

@Repository
public interface MaquinaRepository extends CrudRepository<Maquina, Integer>{
    @Query("Select m from Maquina m where m.nombre = ?1")
    Optional<Maquina> findByNombre(String nombre);
}
