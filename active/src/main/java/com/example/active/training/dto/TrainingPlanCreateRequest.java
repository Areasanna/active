package com.example.active.training.dto;

import com.example.active.training.model.TrainingGoal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record TrainingPlanCreateRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100)
        String name,

        @NotNull(message = "Objetivo é obrigatório")
        TrainingGoal goal,

        @NotNull(message = "Número de semanas é obrigatório")
        @Min(value = 1, message = "Deve ter ao menos 1 semana")
        @Max(value = 52, message = "Máximo de 52 semanas")
        Integer weekCount,

        @NotEmpty(message = "Deve ter ao menos uma semana")
        @Valid
        List<TrainingPlanWeekRequest> weeks
) {
}



