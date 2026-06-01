package com.example.active.dto;

import java.util.List;

public record TrainingPlanWeekResponse(
        Long id,
        Integer weekNumber,
        List<TrainingPlanDayResponse> days) {
}
