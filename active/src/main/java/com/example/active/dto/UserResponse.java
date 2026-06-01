package com.example.active.dto;

import com.example.active.model.Role;
import com.example.active.model.TrainingLevel;
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
        Role role,
        //formatação para a data sair mais legível
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime createdAt

){}
