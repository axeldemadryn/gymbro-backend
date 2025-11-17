package com.gym.backend.business.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.gym.backend.model.MetricaHistoricaDeEjercicio;

@Repository
public interface MetricaHistoricaRepository extends CrudRepository<MetricaHistoricaDeEjercicio, Long>{
    @Query("SELECT m FROM MetricaHistoricaDeEjercicio m WHERE m.usuario.id = ?1")
    List<MetricaHistoricaDeEjercicio> encontrarAsociadasAUsuarioPorId(long idUsuario);
}
