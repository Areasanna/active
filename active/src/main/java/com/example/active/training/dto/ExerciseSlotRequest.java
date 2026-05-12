package com.example.active.training.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ExerciseSlotRequest(
        @NotNull(message = "ID do exercício é obrigatório")
        Long exerciseId,

        @NotNull(message = "Número de séries é obrigatório")
        @Min(value = 1, message = "Mínimo 1 série")
        @Max(value = 20, message = "Máximo 20 séries")
        Integer sets,

        @NotNull(message = "Número de repetições é obrigatório")
        @Min(value = 1, message = "Mínimo 1 repetição")
        @Max(value = 200, message = "Máximo 200 repetições")
        Integer reps,

        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal weightKg,

        @Min(value = 0, message = "Descanso não pode ser negativo")
        @Max(value = 600, message = "Máximo 600 segundos de descanso")
        Integer restSeconds) {
}
