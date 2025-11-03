package com.gym.backend.business.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.Maquina;

@Repository
public interface MaquinaRepository extends CrudRepository<Maquina, Long> {
    @Query("SELECT m FROM Maquina m WHERE LOWER(m.nombre) = LOWER(?1)")
    Optional<Maquina> findByNombre(String nombre);

    Optional<Maquina> findByNombreIgnoreCase(String nombre);

    @Query("""
                SELECT DISTINCT m
                FROM Session s
                JOIN s.sessionExercises se
                JOIN se.exercise e
                JOIN e.maquinas m
                WHERE s.id = ?1
            """)
    List<Maquina> findMaquinasBySessionId(Long sessionId);

    @Query("""
                SELECT DISTINCT m
                FROM Maquina m
                JOIN m.musculos mm
                WHERE mm.id = :musculoId
            """)
    List<Maquina> findMaquinasByMusculoId(@Param("musculoId") Long musculoId);

}
