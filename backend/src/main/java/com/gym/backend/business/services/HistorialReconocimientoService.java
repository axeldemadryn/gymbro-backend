package com.gym.backend.business.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.HistorialReconocimientoRepository;
import com.gym.backend.model.HistorialReconocimiento;

import jakarta.transaction.Transactional;

@Service
public class HistorialReconocimientoService {
    @Autowired
    private HistorialReconocimientoRepository repository;

    public List<HistorialReconocimiento> findAll(){
        List<HistorialReconocimiento> retorno = new ArrayList<>();
        repository.findAll().forEach(retorno::add);
        return retorno;
    }

    public List<HistorialReconocimiento> findAllByUserId(long id){
        return repository.findAllByUserId(id);
    }

    public HistorialReconocimiento findById(long id){
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public HistorialReconocimiento save(HistorialReconocimiento historial){
        return repository.save(historial);
    }

    @Transactional
    public void deleteById(long id){
        repository.deleteById(id);
    }

    @Transactional
    public void deleteAllByUserId(long id){
        repository.deleteAllByUserId(id);
    }
}
