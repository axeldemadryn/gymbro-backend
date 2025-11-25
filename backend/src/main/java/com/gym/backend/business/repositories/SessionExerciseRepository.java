package com.gym.backend.business.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.SessionExercise;

@Repository
public interface SessionExerciseRepository extends CrudRepository<SessionExercise, Long> {

  long countBySessionId(Long id);

  List<SessionExercise> findAllBySessionUserId(Long userId);

  List<SessionExercise> findAllBySessionId(Long sessionId);

  @Query("""
          SELECT COUNT(DISTINCT m.id)
          FROM SessionExercise se
          JOIN se.exercise e
          JOIN e.musculos m
          JOIN m.maquinas mq
          WHERE se.id = :sessionExerciseId
            AND mq.id = :maquinaId
      """)
  long contarCoincidenciasMusculosPorSessionExercise(
      @Param("sessionExerciseId") Long sessionExerciseId,
      @Param("maquinaId") Long maquinaId);

  @Query("""
          SELECT m.nombre
          FROM SessionExercise se
          JOIN se.exercise e
          JOIN e.musculos m
          JOIN Maquina mm
          ON mm.id = :maquinaId
          JOIN mm.musculos m2
          WHERE se.id = :sessionExerciseId AND m.id = m2.id
      """)
  List<String> obtenerMusculosCoincidentes(Long sessionExerciseId, Long maquinaId);

  boolean existsByExerciseId(Long id);

}
