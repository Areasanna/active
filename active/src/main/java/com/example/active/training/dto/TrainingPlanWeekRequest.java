package com.example.active.training.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TrainingPlanWeekRequest(
        @NotNull(message = "Número da semana é obrigatório")
        @Min(1)
        Integer weekNumber,

        @NotEmpty(message = "Deve ter ao menos um dia na semana")
        @Valid
        List<TrainingPlanDayRequest> days) {
}
