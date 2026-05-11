package com.example.active.training;

import com.example.active.training.model.SplitFocus;
import com.example.active.training.model.TrainingGoal;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record TrainingPlanCreateResponse(
            Long id,
            String name,
            TrainingGoal goal,
            Integer weekCount,
            LocalDateTime createdAt,
            List<TrainingPlanWeekResponse> weeks
    ) {

    }

     record TrainingPlanWeekResponse(
            Long id,
            Integer weekNumber,
            List<TrainingPlanDayResponse> days
    ) {
    }

     record TrainingPlanDayResponse(
            Long id,
            DayOfWeek dayOfWeek,
            SplitFocus splitFocus,
            List<ExerciseSlotResponse> exercises
    ) {
    }
    record ExerciseSlotResponse(
            Long id,
            Long exerciseId,
            String exerciseTitle,
            Integer sets,
            Integer reps,
            BigDecimal weightKg,
            Integer restSeconds){}

 record PersonalRecordResponse(
        Long exerciseId,
        String exerciseTitle,
        BigDecimal maxWeightKg,
        Integer maxReps,
        LocalDate achievedAt) {}




