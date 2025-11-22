package com.gym.backend.dto;

import java.util.Map;

import lombok.Data;

@Data
public class EstadisticaSemanalDTO {

    private int totalDiasConSesiones;
    private int sesionesCompletadas;
    private int sesionesPendientes;

    private int totalEjerciciosPlanificados;
    private int ejerciciosCompletados;

    private double porcentajeAdherencia; // sesiones completadas / sesiones totales
    private double porcentajeEjerciciosCompletados;

    private Map<String, Integer> musculosMasTrabajados;
}
