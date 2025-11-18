package com.gym.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "planes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TipoPlan type;

    @Column(nullable = false)
    private Boolean active = true; // Si el plan está habilitado para uso

}
