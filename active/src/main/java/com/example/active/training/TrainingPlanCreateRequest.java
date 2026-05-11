package com.example.active.training;

import com.example.active.training.model.SplitFocus;
import com.example.active.training.model.TrainingGoal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
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

record TrainingPlanWeekRequest(
        @NotNull(message = "Número da semana é obrigatório")
        @Min(1)
        Integer weekNumber,

        @NotEmpty(message = "Deve ter ao menos um dia na semana")
        @Valid
        List<TrainingPlanDayRequest> days
) {
}

record TrainingPlanDayRequest(
        @NotNull(message = "Dia da semana é obrigatório")
        DayOfWeek dayOfWeek,

        @NotNull(message = "Foco do split é obrigatório")
        SplitFocus splitFocus,

        @NotEmpty(message = "Deve ter ao menos um exercício")
        @Valid
        List<ExerciseSlotRequest> exercises
) {
}

record ExerciseSlotRequest(
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
        Integer restSeconds
) {
}

