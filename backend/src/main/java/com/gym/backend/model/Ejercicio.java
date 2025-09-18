package com.gym.backend.model;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity                
@Getter                 
@Setter                
@NoArgsConstructor  
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

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "ejercicio_maquinas", joinColumns = @JoinColumn(name = "ejercicio_id"), inverseJoinColumns = @JoinColumn(name = "maquina_id"))
    @JsonIgnore
    private Collection<Maquina> maquinas;

    // Músculos principales y secundarios (OneToMany unidireccional)
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "ejercicio_musculos_principales", joinColumns = @JoinColumn(name = "ejercicio_id"), inverseJoinColumns = @JoinColumn(name = "musculo_id"))
    private Collection<Musculo> musculosPrincipales;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "ejercicio_musculos_secundarios", joinColumns = @JoinColumn(name = "ejercicio_id"), inverseJoinColumns = @JoinColumn(name = "musculo_id"))
    private Collection<Musculo> musculosSecundarios;

    private boolean esPersonalizado = false;

}
