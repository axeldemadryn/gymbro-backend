package com.gym.backend.business.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.MetricaHistoricaRepository;
import com.gym.backend.model.MetricaHistoricaDeEjercicio;

import jakarta.transaction.Transactional;

@Service
public class MetricaHistoricaService {
    @Autowired
    private MetricaHistoricaRepository repository;

    public List<MetricaHistoricaDeEjercicio> encontrarAsociadasAUsuarioPorId(long idUsuario){
        return repository.encontrarAsociadasAUsuarioPorId(idUsuario);
    }

    @Transactional
    public MetricaHistoricaDeEjercicio save(MetricaHistoricaDeEjercicio metrica){
        return repository.save(metrica);
    }
}
