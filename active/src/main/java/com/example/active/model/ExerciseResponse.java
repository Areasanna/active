package com.example.active.model;

import com.example.active.dto.EquipmentResponse;
import com.example.active.dto.MuscleResponse;

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
