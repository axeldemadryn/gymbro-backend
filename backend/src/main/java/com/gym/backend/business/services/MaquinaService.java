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
@Transactional
public class MaquinaService {
    @Autowired
    private MaquinaRepository repository;

    public Maquina findById(long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Maquina> findAll() {
        List<Maquina> result = new ArrayList<>();
        repository.findAll().forEach(unaMaquina -> result.add(unaMaquina));
        return result;
    }

    public Maquina findByNombre(String nombre) {
        return repository.findByNombreIgnoreCase(nombre).orElse(null);
    }

    @Transactional
    public Maquina save(Maquina maquina) {
        return repository.save(maquina);
    }

    @Transactional
    public void delete(long id) {
        repository.deleteById(id);
    }

    public Mono<MaquinaDTO> obtenerMaquinaConInfo(String nombre) {
        Maquina maquina = findByNombre(nombre);
        if (maquina == null)
            return Mono.empty();

        MaquinaDTO dto = new MaquinaDTO();
        dto.setNombre(maquina.getNombre());
        dto.setTipoEquipo(maquina.getTipoEquipo() != null ? maquina.getTipoEquipo().name() : null);
        dto.setDescripcion(maquina.getDescripcion());
        dto.setImagen(maquina.getImagenUrl());

        // Verificamos si la máquina tiene ejercicios asociados
        if (maquina.getEjercicios() != null && !maquina.getEjercicios().isEmpty()) {
            dto.setEjercicios(
                    maquina.getEjercicios().stream()
                            .map(ej -> {
                                EjercicioDTO ejDTO = new EjercicioDTO();
                                ejDTO.setNombre(ej.getNombre());
                                ejDTO.setTipo(ej.getTipo() != null ? ej.getTipo().name() : null);
                                ejDTO.setDescripcion(ej.getDescripcion());
                                ejDTO.setVideoUrl(ej.getVideoUrl());
                                ejDTO.setMusculosPrincipales(
                                        ej.getMusculos() == null ? List.of()
                                                : ej.getMusculos().stream()
                                                        .map(m -> new MusculoDTO(m.getNombre()))
                                                        .toList());
                                return ejDTO;
                            })
                            .toList());
            dto.setMusculos(null); // no mostramos músculos directos
        } else if (maquina.getMusculos() != null && !maquina.getMusculos().isEmpty()) {
            // Si no tiene ejercicios, mostramos los músculos que trabaja
            dto.setMusculos(
                    maquina.getMusculos().stream()
                            .map(m -> new MusculoDTO(m.getNombre()))
                            .toList());
            dto.setEjercicios(null);
        }

        return Mono.just(dto);
    }

}
