package com.gym.backend.dto;

import java.util.List;

public class MaquinaDTO {
    private String nombre;
    private String tipoEquipo;
    private String descripcion;
    private String imagen;
    private List<EjercicioDTO> ejercicios;
    private List<MusculoDTO> musculos; 

    public MaquinaDTO() {}

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<EjercicioDTO> getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(List<EjercicioDTO> ejercicios) {
        this.ejercicios = ejercicios;
    }

    public List<MusculoDTO> getMusculos() {
        return musculos;
    }

    public void setMusculos(List<MusculoDTO> musculos) {
        this.musculos = musculos;
    }
}
