package com.gym.backend.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "weekly_routines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyRoutine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate startDate; // cuándo empieza la rutina

    private LocalDate endDate; // cuándo termina la rutina

    private Long userId; // Usuario dueño de la rutina
}
