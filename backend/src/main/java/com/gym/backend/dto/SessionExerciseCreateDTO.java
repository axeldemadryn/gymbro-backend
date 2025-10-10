package com.gym.backend.dto;

import lombok.Data;

@Data
public class SessionExerciseCreateDTO {
    private int sets;
    private int reps;
    private Long sessionId;
    private Long exerciseId;
}
