package com.gym.backend.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "El nombre de la máquina no puede ser nulo.")
    @Column(unique = true, nullable = false)
    private String nombre;

    @Column(unique = true)
    private String nombreTraducido;

    @Enumerated(EnumType.STRING)
    private TipoEquipo tipoEquipo;

    private String descripcion;

    private String imagenUrl; // foto genérica o de catálogo

    @ManyToMany(mappedBy = "maquinas")
    @JsonIgnore // evita recursión Ejercicio -> Maquina -> Ejercicio
    private Set<Ejercicio> ejercicios;

    @ManyToMany
    @JoinTable(name = "maquina_musculos", joinColumns = @JoinColumn(name = "maquina_id"), inverseJoinColumns = @JoinColumn(name = "musculo_id"))
    private Set<Musculo> musculos;
}