package com.example.active.exercise.dto;

import com.example.active.exercise.model.ExerciseCategory;
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

