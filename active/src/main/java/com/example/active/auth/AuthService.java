package com.example.active.auth;

import com.example.active.config.TokenUtil;
import com.example.active.user.dto.LoginRequest;
import com.example.active.user.dto.LoginResponse;
import com.example.active.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final TokenUtil tokenUtil;

    public LoginResponse login(LoginRequest request) {
        // Log de entrada
        System.out.println(">>> 1. Recebi o email: " + request.email());

        // Criamos o objeto de autenticação
        var loginData = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        // O ponto mais comum de erro (Senha errada ou usuário não existe)
        System.out.println(">>> 2. Vou pedir para o AuthManager autenticar...");
        Authentication authentication = authManager.authenticate(loginData);

        // Se chegar aqui, a senha estava correta
        System.out.println(">>> 3. Autenticado com sucesso! Fazendo o cast...");
        User user = (User) authentication.getPrincipal();

        // O segundo ponto mais comum de erro (Chave JWT curta demais)
        System.out.println(">>> 4. Vou gerar o token agora...");
        String token = tokenUtil.generateToken(user);

        System.out.println(">>> 5. Token gerado com sucesso!");
        return new LoginResponse(token);
    }
}
