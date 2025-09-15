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

    @OneToMany(mappedBy = "maquina", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Ejercicio> ejercicios;

    // Músculos principales y secundarios
    @ManyToMany
    @JoinTable(name = "maquina_musculos_principales", joinColumns = @JoinColumn(name = "maquina_id"), inverseJoinColumns = @JoinColumn(name = "musculo_id"))
    private Collection<Musculo> musculosPrincipales;

    @ManyToMany
    @JoinTable(name = "maquina_musculos_secundarios", joinColumns = @JoinColumn(name = "maquina_id"), inverseJoinColumns = @JoinColumn(name = "musculo_id"))
    private Collection<Musculo> musculosSecundarios;

    public Collection<Ejercicio> getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(Collection<Ejercicio> ejercicios) {
        this.ejercicios = ejercicios;
    }

    public Collection<Musculo> getMusculosPrincipales() {
        return musculosPrincipales;
    }

    public void setMusculosPrincipales(Collection<Musculo> musculosPrincipales) {
        this.musculosPrincipales = musculosPrincipales;
    }

    public Collection<Musculo> getMusculosSecundarios() {
        return musculosSecundarios;
    }

    public void setMusculosSecundarios(Collection<Musculo> musculosSecundarios) {
        this.musculosSecundarios = musculosSecundarios;
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
