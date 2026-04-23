package com.example.active.usuario;

import java.time.LocalDateTime;

//Classe de resposta
public record StandardError(
    Integer status,
    String message,
    LocalDateTime timestamp
){}
