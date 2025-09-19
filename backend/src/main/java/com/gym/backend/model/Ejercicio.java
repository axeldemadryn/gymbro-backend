package com.gym.backend.model;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    @JoinColumn(name = "id_ejercicio_musculo_principal_de")
    private Collection<Musculo> musculosPrincipales;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "id_ejercicio_musculo_secundario_de")
    private Collection<Musculo> musculosSecundarios;

    private boolean esPersonalizado = false;

}
