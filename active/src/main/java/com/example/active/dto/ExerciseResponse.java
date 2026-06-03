package com.example.active.dto;

import com.example.active.model.ExerciseCategory;

import java.util.List;

public record ExerciseResponse(
        Long id,
        String title,
        String description,
        String videoUrl,
        ExerciseCategory category,
        List<EquipmentResponse> equipment,
        List<MuscleResponse> primaryMuscles,
        List<MuscleResponse> secondaryMuscles) {
}
