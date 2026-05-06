package com.example.active.exercicios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ExerciseUpdateRequest(
        @NotBlank
        String title,

        @NotBlank
        String description,

        String videoUrl,

        @NotNull
        ExerciseCategory category,

        @NotEmpty
        List<Long> equipmentIds,

        @NotEmpty
        List<Long> primaryMuscleIds,
        List<Long> secondaryMuscleIds) {
}

