package com.example.active.service;

import com.example.active.config.TokenUtil;
import com.example.active.dto.LoginRequest;
import com.example.active.dto.LoginResponse;
import com.example.active.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final TokenUtil tokenUtil;

    public LoginResponse login(LoginRequest request) {
        try {
            // 1. Tenta autenticar o usuário
            var loginData = new UsernamePasswordAuthenticationToken(request.email(), request.password());
            Authentication authentication = authManager.authenticate(loginData);

            // 2. Extrai o usuário autenticado
            User user = (User) authentication.getPrincipal();

            // 3. Gera o token
            String token = tokenUtil.generateToken(user);

            return new LoginResponse(token);

        } catch (BadCredentialsException e) {
            // Caso o e-mail ou a senha estejam incorretos
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
        } catch (Exception e) {
            // Caso ocorra qualquer outro erro (como erro na geração do token)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao processar o login: " + e.getMessage());
        }
    }
}
