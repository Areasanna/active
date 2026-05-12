package com.example.active.training.dto;

import java.util.List;

public record TrainingPlanWeekResponse(
        Long id,
        Integer weekNumber,
        List<TrainingPlanDayResponse> days) {
}
