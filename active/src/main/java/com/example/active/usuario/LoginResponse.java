package com.example.active.usuario;

public record LoginResponse(
        String token,
        String tokenType) {
}
