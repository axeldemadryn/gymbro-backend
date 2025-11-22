package com.gym.backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class ExerciseRecommendationsDTO {

    private Long exerciseId;
    private String exerciseName;

    private Integer sets;
    private Integer reps;

    private List<MusculoDTO> musculos;

    private List<RecomendacionDTO> recommendedMachines;
}
