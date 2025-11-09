package com.gym.backend.business.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.Ejercicio;
import com.gym.backend.model.TipoEjercicio;

@Repository
public interface EjercicioRepository extends CrudRepository<Ejercicio, Long> {

       List<Ejercicio> findByUserId(Long userId);

       boolean existsByNombreIgnoreCase(String nombre);

       // Buscar ejercicios por tipo (FUERZA, CARDIO, MOVILIDAD, ESTIRAMIENTO)
       List<Ejercicio> findByTipo(TipoEjercicio tipo);

       // Ejemplo de query personalizada para traer ejercicio con músculos y máquinas
       // (JOIN FETCH)
       @Query("SELECT e FROM Ejercicio e " +
                     "LEFT JOIN FETCH e.maquinas " +
                     "LEFT JOIN FETCH e.musculos " +
                     "WHERE e.id = :id")
       Optional<Ejercicio> findByIdConDetalles(@Param("id") Long id);

       // Traer todos los ejercicios con sus músculos y máquinas
       @Query("SELECT DISTINCT e FROM Ejercicio e " +
                     "LEFT JOIN FETCH e.maquinas " +
                     "LEFT JOIN FETCH e.musculos ")
       List<Ejercicio> findAllConDetalles();

       @Query("SELECT e FROM Ejercicio e " +
                     "WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) " +
                     "AND (e.user IS NULL OR e.user.id = :userId)")
       List<Ejercicio> findByNombreContainingIgnoreCaseAndUserIsNullOrUserId(
                     @Param("nombre") String nombre,
                     @Param("userId") Long userId);

       @Query("SELECT e FROM Ejercicio e WHERE e.nombre = ?1")
       Optional<Ejercicio> findByNombre(String nombre);

       boolean existsByNombreAndUserIsNull(String nombre);

       @Query("SELECT e FROM Ejercicio e WHERE e.nombre = :nombre AND (e.user IS NULL OR e.user.id = :userId)")
       Optional<Ejercicio> findByNombreAndUserIdOrGlobal(@Param("nombre") String nombre, @Param("userId") Long userId);

       List<Ejercicio> findByUserIsNullOrUserId(Long userId);

       @Query("SELECT e FROM Ejercicio e WHERE e.tipo = :tipo AND (e.user.id = :userId OR e.user IS NULL)")
       List<Ejercicio> findByTipoAndUserOrGlobal(@Param("tipo") TipoEjercicio tipo, @Param("userId") Long userId);

}
