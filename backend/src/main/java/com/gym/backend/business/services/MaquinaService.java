package com.gym.backend.business.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.MaquinaRepository;
import com.gym.backend.model.Maquina;

import jakarta.transaction.Transactional;

@Service
public class MaquinaService {
    @Autowired
    MaquinaRepository repository;

    public Maquina findById(long id){
        return repository.findById((int)id).orElse(null);
    }

    public List<Maquina> findAll(){
        List<Maquina> result = new ArrayList<>();
        repository.findAll().forEach(unaMaquina -> result.add(unaMaquina));
        return result;
    }

    public Maquina findByNombre(String nombre){
        return repository.findByNombre(nombre).orElse(null);
    }

    @Transactional
    public Maquina save(Maquina maquina){
        return repository.save(maquina);
    }

    @Transactional
    public void delete(long id){
        repository.deleteById((int)id);
    }
}
