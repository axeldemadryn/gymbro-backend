package com.gym.backend.dto;

public class MusculoDTO {
    private String nombre;

    public MusculoDTO() {
    }

    public MusculoDTO(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
