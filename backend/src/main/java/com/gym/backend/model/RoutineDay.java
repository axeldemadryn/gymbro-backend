package com.gym.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "routine_days", uniqueConstraints = @UniqueConstraint(columnNames = { "routine_id", "day" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoutineDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DiaDeSemana day; // Lunes, Martes, ...

    @ManyToOne
    @JoinColumn(name = "routine_id")
    private WeeklyRoutine routine;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;

    @Enumerated(EnumType.STRING)
    private SessionStatus status = SessionStatus.PENDIENTE;
}
