package com.gym.backend.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = true)
    private LocalDate startDate; // cuándo empieza la rutina

    @Column(nullable = true)
    private LocalDate endDate; // cuándo termina la rutina

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
