package com.example.active.usuario;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;


public record UsuarioResponse (
        Long id,
        String nome,
        String email,
        TrainingLevel trainingLevel,
        //formatação para a data sair mais legível
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime createdAt

){}
