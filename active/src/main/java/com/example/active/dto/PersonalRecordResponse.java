package com.example.active.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PersonalRecordResponse(
        Long exerciseId,
        String exerciseTitle,
        BigDecimal maxWeightKg,
        Integer maxReps,
        LocalDate achievedAt) {
}
