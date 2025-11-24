package com.gym.backend.model;

import lombok.Getter;

@Getter
public enum TipoPlan {
        GRATUITO(
                        2,
                        1,
                        4,
                        4,
                        true,
                        0.0),
        PREMIUM(
                        Integer.MAX_VALUE,
                        10,
                        Integer.MAX_VALUE,
                        7,
                        true,
                        2000.0);

        private int maxReconocimientos; // Máximo de usos del sistema de reconocimiento de máquinas por imágenes
        private int maxReconocimientosDiarios; // Máximo de usos, en un día, del sistema de reconocimiento de máquinas por imágenes
        private int maxRutinasSemanales; // Máximo de rutinas semanales asociadas a un usuario
        private int maxSesionesPorSemana; // Máximo de rutinas diarias por semana
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
