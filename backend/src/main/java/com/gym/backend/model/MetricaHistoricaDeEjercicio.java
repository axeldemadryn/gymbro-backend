package com.gym.backend.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metricas_historicas_de_ejercicio", uniqueConstraints = @UniqueConstraint(columnNames = { "usuario_id", "ejercicio_id", "fecha" }))
public class MetricaHistoricaDeEjercicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @NotNull(message = "La métrica debe estar asociada a un usuario.")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "ejercicio_id")
    @NotNull(message = "La métrica debe estar asociada a un ejercicio.")
    private Ejercicio ejercicio;

    @NotNull(message = "La fecha de la métrica no puede ser nula.")
    private LocalDate fecha;

    private double pesoUsado;

    private int repsHechas;

    private int seriesHechas;
}
