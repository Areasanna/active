package com.example.active.usuario;

import java.time.LocalDateTime;

public record UsuarioResponse (
        Long id,
        String nome,
        String email,
        TrainingLevel trainingLevel,
        LocalDateTime creatadAt

){}
