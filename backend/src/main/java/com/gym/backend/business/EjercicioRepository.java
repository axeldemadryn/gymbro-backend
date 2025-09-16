package com.gym.backend.business;

import com.gym.backend.model.Ejercicio;
import com.gym.backend.model.TipoEjercicio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EjercicioRepository extends CrudRepository<Ejercicio, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    // Buscar ejercicios por tipo (FUERZA, CARDIO, MOVILIDAD, ESTIRAMIENTO)
    List<Ejercicio> findByTipo(TipoEjercicio tipo);

    // Buscar ejercicios solo personalizados o globales
    List<Ejercicio> findByEsPersonalizado(boolean esPersonalizado);

    // Ejemplo de query personalizada para traer ejercicio con músculos y máquinas (JOIN FETCH)
    @Query("SELECT e FROM Ejercicio e " +
           "LEFT JOIN FETCH e.maquinas " +
           "LEFT JOIN FETCH e.musculosPrincipales " +
           "LEFT JOIN FETCH e.musculosSecundarios " +
           "WHERE e.id = :id")
    Optional<Ejercicio> findByIdConDetalles(@Param("id") Long id);

    // Traer todos los ejercicios con sus músculos y máquinas
    @Query("SELECT DISTINCT e FROM Ejercicio e " +
           "LEFT JOIN FETCH e.maquinas " +
           "LEFT JOIN FETCH e.musculosPrincipales " +
           "LEFT JOIN FETCH e.musculosSecundarios")
    List<Ejercicio> findAllConDetalles();

    List<Ejercicio> findByNombreContainingIgnoreCase(String nombre);
}

