package com.gym.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
    private Boolean canceled = false;
}
