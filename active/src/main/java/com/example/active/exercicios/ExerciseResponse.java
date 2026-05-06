package com.example.active.exercicios;

import com.example.active.equipment.EquipmentResponse;
import com.example.active.muscle.MuscleResponse;

import java.util.List;

public record ExerciseResponse(
        Long id,
        String title,
        String videoUrl,
        String description,
        ExerciseCategory category,
        List<EquipmentResponse> equipment,
        List<MuscleResponse> primaryMuscle,
        List<MuscleResponse> secondaryMuscle) {
}
