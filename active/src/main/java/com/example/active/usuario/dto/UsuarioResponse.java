package com.example.active.usuario.dto;

import com.example.active.usuario.TrainingLevel;

import java.time.LocalDateTime;

public record UsuarioResponse (
        Long ig,
        String nome,
        String email,
        TrainingLevel trainingLevel,
        LocalDateTime creatadAt
){}
