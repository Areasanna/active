package com.example.active.exercicios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import javax.swing.*;
import java.util.List;

public record ExerciseRequest(
        @NotBlank
        String title,
        @NotBlank
        String description,
        String videoUrl,
        @NotNull
        Category category,
        List<Long>equipmentIds,
        List<Long>musculoPrimario,
        List<Long>musculoSegundario
){}

