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
