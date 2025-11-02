package com.gym.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecomendacionDTO {
    private MaquinaDTO maquina;
    private double nivelCoincidencia; // 0 a 100
    private String mensaje; // "No recomendable", "Coincidencia parcial", "Altamente recomendable"
}
