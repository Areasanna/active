package com.example.active.training.dto;

import com.example.active.training.model.TrainingGoal;

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





