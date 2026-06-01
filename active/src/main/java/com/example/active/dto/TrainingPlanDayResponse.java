package com.example.active.dto;

import com.example.active.model.SplitFocus;

import java.time.DayOfWeek;
import java.util.List;

public record TrainingPlanDayResponse(
        Long id,
        DayOfWeek dayOfWeek,
        SplitFocus splitFocus,
        List<ExerciseSlotResponse> exercises) {
}
