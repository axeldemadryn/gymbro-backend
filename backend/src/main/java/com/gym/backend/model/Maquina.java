package com.gym.backend.model;

import jakarta.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "maquinas")
public class Maquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // nombre que devuelve RoboFlow / OpenAI

    @Enumerated(EnumType.STRING)
    private TipoEquipo tipoEquipo;

    private String descripcion; // breve descripción de uso

    private String videoUrl; // URL de video o animación instructiva

    @ManyToMany(mappedBy = "maquinas", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Collection<Ejercicio> ejercicios;

    private boolean esPersonalizado = false;

    public boolean isEsPersonalizado() {
        return esPersonalizado;
    }

    public void setEsPersonalizado(boolean esPersonalizado) {
        this.esPersonalizado = esPersonalizado;
    }

    public Collection<Ejercicio> getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(Collection<Ejercicio> ejercicios) {
        this.ejercicios = ejercicios;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoEquipo getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(TipoEquipo tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

}
