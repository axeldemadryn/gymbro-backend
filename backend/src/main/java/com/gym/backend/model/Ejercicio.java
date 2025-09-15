package com.gym.backend.model;

import java.util.Collection;

import jakarta.persistence.*;

@Entity
@Table(name = "ejercicios")
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoEjercicio tipo; // FUERZA, CARDIO, MOVILIDAD, ESTIRAMIENTO

    private String descripcion;

    private String videoUrl;

    @ManyToMany
    @JoinTable(name = "ejercicio_maquinas", joinColumns = @JoinColumn(name = "ejercicio_id"), inverseJoinColumns = @JoinColumn(name = "maquina_id"))
    private Collection<Maquina> maquinas;

    // Músculos principales y secundarios
    @ManyToMany
    @JoinTable(name = "ejercicio_musculos_principales", joinColumns = @JoinColumn(name = "ejercicio_id"), inverseJoinColumns = @JoinColumn(name = "musculo_id"))
    private Collection<Musculo> musculosPrincipales;

    @ManyToMany
    @JoinTable(name = "ejercicio_musculos_principales", joinColumns = @JoinColumn(name = "ejercicio_id"), inverseJoinColumns = @JoinColumn(name = "musculo_id"))
    private Collection<Musculo> musculosSecundarios;

    private boolean esPersonalizado = false;

    public boolean isEsPersonalizado() {
        return esPersonalizado;
    }

    public void setEsPersonalizado(boolean esPersonalizado) {
        this.esPersonalizado = esPersonalizado;
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

    public Collection<Maquina> getMaquinas() {
        return maquinas;
    }

    public void setMaquinas(Collection<Maquina> maquinas) {
        this.maquinas = maquinas;
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

    public TipoEjercicio getTipo() {
        return tipo;
    }

    public void setTipo(TipoEjercicio tipo) {
        this.tipo = tipo;
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
