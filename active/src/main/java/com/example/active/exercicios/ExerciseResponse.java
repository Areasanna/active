package com.example.active.exercicios;

import java.util.List;

public record ExerciseResponse(
        Long id,
        String title,
        String videoUrl,
        String description,
        Category category,
        List<String> equipmentNames,
        List<String> musculoPrimario,
        List<String>musculoSecundario
) {}
