package com.gym.backend.model;

import lombok.Getter;

@Getter
public enum TipoPlan {
    GRATUITO(
            10,
            10,
            10,
            7,
            true,
            0.0),
    PREMIUM(
            Integer.MAX_VALUE,
            20,
            Integer.MAX_VALUE,
            7,
            true,
            30000.0);

    private int maxReconocimientos;
    private int maxReconocimientosDiarios;
    private int maxRutinas;
    private int maxSesionesPorSemana;
    private Boolean permiteEstadisticas = false;
    private double precioEnPesos;

    TipoPlan(int maxReconocimientos, int maxReconocimientosDiarios, int maxRutinas, int maxSesionesPorSemana,
            Boolean permiteEstadisticas, double precioEnPesos) {
        this.maxReconocimientos = maxReconocimientos;
        this.maxReconocimientosDiarios = maxReconocimientosDiarios;
        this.maxRutinas = maxRutinas;
        this.maxSesionesPorSemana = maxSesionesPorSemana;
        this.precioEnPesos = precioEnPesos;
        this.permiteEstadisticas = permiteEstadisticas;
    }
}
