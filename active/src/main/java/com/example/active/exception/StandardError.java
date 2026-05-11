package com.example.active.exception;

import java.time.LocalDateTime;

//Classe de resposta
public record StandardError(
    Integer status,
    String message,
    LocalDateTime timestamp
){}
