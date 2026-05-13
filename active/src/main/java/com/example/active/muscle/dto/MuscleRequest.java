package com.example.active.muscle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MuscleRequest(
        @NotBlank(message = "O nome do músculo é obrigatório")
        @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
        String name,

        @NotBlank(message = "O nome em inglês é obrigatório")
        String nameEn) {
}

