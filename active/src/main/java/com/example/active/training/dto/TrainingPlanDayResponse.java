package com.example.active.training.dto;

import com.example.active.training.model.SplitFocus;

import java.time.DayOfWeek;
import java.util.List;

public record TrainingPlanDayResponse(
        Long id,
        DayOfWeek dayOfWeek,
        SplitFocus splitFocus,
        List<ExerciseSlotResponse> exercises) {
}
