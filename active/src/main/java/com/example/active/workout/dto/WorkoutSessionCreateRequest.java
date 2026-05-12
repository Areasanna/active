package com.example.active.workout.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class WorkoutSessionCreateRequest {
    public record Create(
            Long trainingPlanDayId,

            @NotNull(message = "Data é obrigatória")
            LocalDate date,

            @NotEmpty(message = "Deve ter ao menos um exercício")
            @Valid
            List<SessionExerciseRequest> exercises
    ) {
    }

    public record SessionExerciseRequest(
            @NotNull(message = "ID do exercício é obrigatório")
            Long exerciseId,

            @NotEmpty(message = "Deve ter ao menos uma série")
            @Valid
            List<SessionSetRequest> sets
    ) {
    }

    public record SessionSetRequest(
            @NotNull(message = "Repetições são obrigatórias")
            @Min(value = 1, message = "Mínimo 1 repetição")
            Integer reps,

            @NotNull(message = "Peso é obrigatório")
            @DecimalMin(value = "0.0", inclusive = true, message = "Peso não pode ser negativo")
            BigDecimal weightKg
    ) {
    }
}
