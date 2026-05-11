package com.example.active.user;

public record LoginResponse(
        String token,
        String tokenType) {
}
