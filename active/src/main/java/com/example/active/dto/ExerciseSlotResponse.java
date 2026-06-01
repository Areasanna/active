package com.example.active.dto;

import java.math.BigDecimal;

public record ExerciseSlotResponse(
        Long id,
        Long exerciseId,
        String exerciseTitle,
        Integer sets,
        Integer reps,
        BigDecimal weightKg,
        Integer restSeconds) {
}
