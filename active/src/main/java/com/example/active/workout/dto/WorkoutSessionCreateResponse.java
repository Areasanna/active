package com.example.active.workout.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class WorkoutSessionCreateResponse {
    public record WorkoutSessionResponse(
            Long id,
            Long trainingPlanDayId,
            LocalDate date,
            int totalExercises,
            LocalDateTime createdAt,
            List<WorkoutExerciseResponse> exercises
    ) {
    }


    public record WorkoutExerciseResponse(
            Long id,
            Long exerciseId,
            String exerciseTitle,
            List<SessionSetResponse> sets
    ) {

    }

    public record SessionSetResponse(
            Long id,
            Integer reps,
            BigDecimal weightKg
    ) {

    }

    public record PersonalRecord(
            Long exerciseId,
            String exerciseTitle,
            BigDecimal maxWeightKg,
            Integer maxReps,
            LocalDate achievedAt
    ) {
    }
}
