package com.example.active.dto;

import com.example.active.model.TrainingGoal;

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





