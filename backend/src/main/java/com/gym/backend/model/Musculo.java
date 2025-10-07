package com.gym.backend.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
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

    // Relación inversa con Ejercicio
    @ManyToMany(mappedBy = "musculos")
    @JsonIgnore
    private Set<Ejercicio> ejercicios;

    // Relación inversa con Maquina
    @ManyToMany(mappedBy = "musculos")
    @JsonIgnore
    private Set<Maquina> maquinas;
    
}
