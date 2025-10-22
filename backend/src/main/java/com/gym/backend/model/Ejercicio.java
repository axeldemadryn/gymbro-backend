package com.gym.backend.model;

import java.util.Set;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ejercicios", uniqueConstraints = @UniqueConstraint(columnNames = { "nombre", "user_id" }))
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoEjercicio tipo;

    private String descripcion;

    private String videoUrl;

    @ManyToMany
    @JoinTable(name = "ejercicio_maquinas", joinColumns = @JoinColumn(name = "ejercicio_id"), inverseJoinColumns = @JoinColumn(name = "maquina_id"))
    private Set<Maquina> maquinas;

    @ManyToMany
    @JoinTable(name = "ejercicio_musculos", joinColumns = @JoinColumn(name = "ejercicio_id"), inverseJoinColumns = @JoinColumn(name = "musculo_id"))
    private Set<Musculo> musculos;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
}
