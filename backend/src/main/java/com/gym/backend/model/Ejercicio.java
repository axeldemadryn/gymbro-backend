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

    @Column(unique = true, nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoEjercicio tipo;

    private String descripcion;
    private String videoUrl;

    @ManyToMany
    @JoinTable(
        name = "ejercicio_maquinas",
        joinColumns = @JoinColumn(name = "ejercicio_id"),
        inverseJoinColumns = @JoinColumn(name = "maquina_id")
    )
    private Set<Maquina> maquinas;

    @ManyToMany
    @JoinTable(
        name = "ejercicio_musculos",
        joinColumns = @JoinColumn(name = "ejercicio_id"),
        inverseJoinColumns = @JoinColumn(name = "musculo_id")
    )
    private Set<Musculo> musculos;

    private boolean esPersonalizado = false;
}
