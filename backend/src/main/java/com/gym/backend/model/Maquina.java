package com.gym.backend.model;

import java.util.Collection;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "maquinas")
public class Maquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
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
