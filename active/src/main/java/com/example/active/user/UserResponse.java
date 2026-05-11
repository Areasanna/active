package com.example.active.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        TrainingLevel trainingLevel,
        Integer age,
        BigDecimal weight,
        BigDecimal height,
        //formatação para a data sair mais legível
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime createdAt
){}
