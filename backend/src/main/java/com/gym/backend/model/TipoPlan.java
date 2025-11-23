package com.gym.backend.model;

import lombok.Getter;

@Getter
public enum TipoPlan {
        GRATUITO(
                        2,
                        1,
                        4,
                        7,
                        true,
                        0.0),
        PREMIUM(
                        Integer.MAX_VALUE,
                        10,
                        Integer.MAX_VALUE,
                        7,
                        true,
                        2000.0);

        private int maxReconocimientos;
        private int maxReconocimientosDiarios;
        private int maxRutinasSemanales;
        private int maxSesionesPorSemana;
        private boolean permiteEstadisticas;
        private double precioEnPesos;

        TipoPlan(int maxReconocimientos, int maxReconocimientosDiarios, int maxRutinasSemanales,
                        int maxSesionesPorSemana,
                        boolean permiteEstadisticas, double precioEnPesos) {
                this.maxReconocimientos = maxReconocimientos;
                this.maxReconocimientosDiarios = maxReconocimientosDiarios;
                this.maxRutinasSemanales = maxRutinasSemanales;
                this.maxSesionesPorSemana = maxSesionesPorSemana;
                this.precioEnPesos = precioEnPesos;
                this.permiteEstadisticas = permiteEstadisticas;
        }
}
