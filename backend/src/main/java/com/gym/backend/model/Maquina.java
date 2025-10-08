package com.gym.backend.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;

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
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class, 
    property = "id")
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

    @ManyToMany(mappedBy = "maquinas")
    private Set<Ejercicio> ejercicios;

    @ManyToMany
    @JoinTable(name = "maquina_musculos", joinColumns = @JoinColumn(name = "maquina_id"), inverseJoinColumns = @JoinColumn(name = "musculo_id"))
    private Set<Musculo> musculos;

}
