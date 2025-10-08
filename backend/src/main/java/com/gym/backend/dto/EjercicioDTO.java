package com.gym.backend.dto;

import java.util.List;

public class EjercicioDTO {
    private String nombre;
    private String tipo; // Enum como String
    private String descripcion;
    private String videoUrl;
    private List<MusculoDTO> musculosPrincipales;

    public EjercicioDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public List<MusculoDTO> getMusculosPrincipales() { return musculosPrincipales; }
    public void setMusculosPrincipales(List<MusculoDTO> musculosPrincipales) { this.musculosPrincipales = musculosPrincipales; }

}
