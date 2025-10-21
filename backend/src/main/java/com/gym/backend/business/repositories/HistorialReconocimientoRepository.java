package com.gym.backend.business.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.HistorialReconocimiento;

@Repository
public interface HistorialReconocimientoRepository extends CrudRepository<HistorialReconocimiento, Long>{
    @Query("SELECT h FROM HistorialReconocimiento h WHERE h.usuario.id = ?1")
    List<HistorialReconocimiento> findAllByUserId(long id);

    @Query("DELETE FROM HistorialReconocimiento h WHERE h.usuario.id = ?1")
    void deleteAllByUserId(long id);
}
