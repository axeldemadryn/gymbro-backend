package com.gym.backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class RecomendacionEjercicioDiaDTO {

    private Long ejercicioId;
    private String ejercicioNombre;

    private Integer sets;
    private Integer reps;

    private List<MusculoDTO> musculos;

    private double nivelCoincidencia;
    private String mensaje;
}
