package com.gym.backend.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "musculos")
public class Musculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @ManyToMany(mappedBy = "musculos")
    @JsonIgnore
    private Set<Ejercicio> ejercicios;

    @ManyToMany(mappedBy = "musculos")
    @JsonIgnore
    private Set<Maquina> maquinas;

    public Musculo(String nombre){
        this.nombre = nombre;
    }
}