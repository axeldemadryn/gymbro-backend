package com.gym.backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class RecomendacionPorDiaDTO {

    private MaquinaDTO maquina;
    private List<RecomendacionEjercicioDiaDTO> recomendaciones;

}
