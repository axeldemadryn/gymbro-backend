package com.gym.backend.business.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.Maquina;

@Repository
public interface MaquinaRepository extends CrudRepository<Maquina, Long>{
    @Query("SELECT m FROM Maquina m WHERE LOWER(m.nombre) = LOWER(?1)")
    Optional<Maquina> findByNombre(String nombre);

    Optional<Maquina> findByNombreIgnoreCase(String nombre);
}
