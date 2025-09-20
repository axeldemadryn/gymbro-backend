package com.gym.backend.business.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.MusculoRepository;
import com.gym.backend.model.Musculo;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class MusculoService {

    @Autowired
    private MusculoRepository repository;

    public Musculo findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Musculo findByNombre(String nombre) {
        return repository.findByNombreIgnoreCase(nombre).orElse(null);
    }

    public List<Musculo> findAll() {
        List<Musculo> result = new ArrayList<>();
        repository.findAll().forEach(result::add);
        return result;
    }

    @Transactional
    public Musculo save(Musculo musculo) {
        return repository.save(musculo);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
