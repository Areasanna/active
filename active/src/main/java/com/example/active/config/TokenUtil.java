package com.example.active.config;

import com.example.active.user.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenUtil {

    private final SecretKey key;
    private final long expirationTime;

    // Usando construtor para garantir que a chave seja gerada no boot
    public TokenUtil(@Value("${jwt.secret}") String secret,
                     @Value("${jwt.expiration:3600000}") long expirationTime) {

        // Garante que a chave tem o tamanho correto e é inicializada uma vez
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expirationTime;
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .issuer("ActiveAPI")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public String validateAndGetUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
