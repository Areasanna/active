package com.example.active.user.dto;

public record LoginResponse(
        String token,
        String tokenType) {
}
