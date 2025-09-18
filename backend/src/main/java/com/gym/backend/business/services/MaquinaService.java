package com.gym.backend.business.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.MaquinaRepository;
import com.gym.backend.dto.EjercicioDTO;
import com.gym.backend.dto.MaquinaDTO;
import com.gym.backend.dto.MusculoDTO;
import com.gym.backend.model.Maquina;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
public class MaquinaService {
    @Autowired
    private MaquinaRepository repository;

    public Maquina findById(long id) {
        return repository.findById((int) id).orElse(null);
    }

    public List<Maquina> findAll() {
        List<Maquina> result = new ArrayList<>();
        repository.findAll().forEach(unaMaquina -> result.add(unaMaquina));
        return result;
    }

    public Maquina findByNombre(String nombre) {
        return repository.findByNombre(nombre).orElse(null);
    }

    @Transactional
    public Maquina save(Maquina maquina) {
        return repository.save(maquina);
    }

    @Transactional
    public void delete(long id) {
        repository.deleteById((int) id);
    }

    public Mono<MaquinaDTO> obtenerMaquinaConEjercicios(String nombre) {
        Maquina maquina = findByNombre(nombre);
        if (maquina == null)
            return Mono.empty();

        MaquinaDTO dto = new MaquinaDTO();
        dto.setNombre(maquina.getNombre());
  //      dto.setTipoEquipo(maquina.getTipoEquipo().name());
        dto.setDescripcion(maquina.getDescripcion());
/* 
        List<EjercicioDTO> ejerciciosDTO = maquina.getEjercicios().stream()
                .map(ej -> {
                    EjercicioDTO ejDTO = new EjercicioDTO();
                    ejDTO.setNombre(ej.getNombre());
                    ejDTO.setTipo(ej.getTipo().name());
                    ejDTO.setDescripcion(ej.getDescripcion());
                    ejDTO.setVideoUrl(ej.getVideoUrl());

                    ejDTO.setMusculosPrincipales(
                            ej.getMusculosPrincipales().stream()
                                    .map(m -> {
                                        MusculoDTO mDto = new MusculoDTO();
                                        mDto.setNombre(m.getNombre());
                                        return mDto;
                                    }).toList());

                    ejDTO.setMusculosSecundarios(
                            ej.getMusculosSecundarios().stream()
                                    .map(m -> {
                                        MusculoDTO mDto = new MusculoDTO();
                                        mDto.setNombre(m.getNombre());
                                        return mDto;
                                    }).toList());

                    return ejDTO;
                }).toList();

        dto.setEjercicios(ejerciciosDTO);*/
        return Mono.just(dto);
    }

}
