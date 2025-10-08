package com.gym.backend.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "musculos")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Musculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    // Relación inversa con Ejercicio
    @ManyToMany(mappedBy = "musculos")
    private Set<Ejercicio> ejercicios;

    // Relación inversa con Maquina
    @ManyToMany(mappedBy = "musculos")
    private Set<Maquina> maquinas;

}
