package com.example.active.dto;

import com.example.active.model.SplitFocus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.util.List;

public record TrainingPlanDayRequest(
        @NotNull(message = "Dia da semana é obrigatório")
        DayOfWeek dayOfWeek,

        @NotNull(message = "Foco do split é obrigatório")
        SplitFocus splitFocus,

        @NotEmpty(message = "Deve ter ao menos um exercício")
        @Valid
        List<ExerciseSlotRequest> exercises) {
}
