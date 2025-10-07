package com.gym.backend.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    private String imagenUrl; // URL de imagen, video o animación instructiva

    // Relación con ejercicios (para MULTIFUNCION, PESO_LIBRE, ACCESORIO)
    @ManyToMany(mappedBy = "maquinas")
    @JsonBackReference
    private Set<Ejercicio> ejercicios;

    // Relación con músculos (para equipos AISLADOS)
    @ManyToMany
    @JoinTable(name = "maquina_musculos", joinColumns = @JoinColumn(name = "maquina_id"), inverseJoinColumns = @JoinColumn(name = "musculo_id"))
     @JsonManagedReference
    private Set<Musculo> musculos;

}
