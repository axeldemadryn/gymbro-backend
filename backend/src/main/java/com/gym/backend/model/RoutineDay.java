package com.gym.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutineDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dayOfWeek; // Lunes, Martes, ...

    @ManyToOne
    @JoinColumn(name = "routine_id")
    private WeeklyRoutine routine;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;

    @Enumerated(EnumType.STRING)
    private SessionStatus status = SessionStatus.PENDIENTE;
}
