package com.gym.backend.business.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.gym.backend.model.ReconocimientoUso;

public interface ReconocimientoUsoRepository extends CrudRepository<ReconocimientoUso, Long> {

    Optional<ReconocimientoUso> findByUserIdAndFecha(Long userId, LocalDate fecha);

    @Query("SELECT COALESCE(SUM(r.cantidadUsos), 0) FROM ReconocimientoUso r WHERE r.user.id = :userId")
    long sumarUsosTotales(Long userId);

}
