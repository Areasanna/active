package com.example.active.exercise.dto;

import com.example.active.equipment.dto.EquipmentResponse;
import com.example.active.exercise.model.ExerciseCategory;
import com.example.active.muscle.dto.MuscleResponse;

import java.util.List;

public record ExerciseResponse(
        Long id,
        String title,
        String videoUrl,
        String description,
        ExerciseCategory category,
        List<EquipmentResponse> equipment,
        List<MuscleResponse> primaryMuscles,
        List<MuscleResponse> secondaryMuscles) {
}
