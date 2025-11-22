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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_planes", uniqueConstraints = {
        // Garantiza que solo haya 1 plan ACTIVO por usuario
        @UniqueConstraint(columnNames = { "user_id", "canceled" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    private LocalDate startDate;

    private LocalDate endDate;

    // false = el plan sigue activo
    // true = el plan está terminado
    private boolean canceled = false;
}
