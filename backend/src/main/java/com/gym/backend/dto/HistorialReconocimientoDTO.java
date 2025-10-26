package com.gym.backend.dto;

import java.time.LocalDate;

public class HistorialReconocimientoDTO {
    private Long id;
    private LocalDate fechaReconocimiento;
    private MaquinaDTO detalleReconocimiento; // toda la info de la máquina

    public HistorialReconocimientoDTO() {
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaReconocimiento() {
        return fechaReconocimiento;
    }

    public void setFechaReconocimiento(LocalDate fechaReconocimiento) {
        this.fechaReconocimiento = fechaReconocimiento;
    }

    public MaquinaDTO getDetalleReconocimiento() {
        return detalleReconocimiento;
    }

    public void setDetalleReconocimiento(MaquinaDTO detalleReconocimiento) {
        this.detalleReconocimiento = detalleReconocimiento;
    }
}
