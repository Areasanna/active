package com.example.active.auth;

import com.example.active.config.TokenUtil;
import com.example.active.user.dto.LoginRequest;
import com.example.active.user.dto.LoginResponse;
import com.example.active.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private TokenUtil tokenUtil;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private User mockUser;
    private Authentication mockAuthentication;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("usuario@email.com", "senha123");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("usuario@email.com");

        mockAuthentication = mock(Authentication.class);
    }

    @Nested
    @DisplayName("Testes do método login")
    class LoginTests {

        @Test
        @DisplayName("Deve efetuar login com sucesso e retornar o TokenJwt quando as credenciais forem válidas")
        void login_WithValidCredentials_ShouldReturnLoginResponse() {
            // Arrange
            String tokenGerado = "jwt-token-valido-ficticio";

            // Simula o Spring Security autenticando e devolvendo o nosso User customizado no principal
            when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuthentication);
            when(mockAuthentication.getPrincipal()).thenReturn(mockUser);
            when(tokenUtil.generateToken(mockUser)).thenReturn(tokenGerado);

            // Act
            LoginResponse response = authService.login(loginRequest);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.token()).isEqualTo(tokenGerado);

            // Verifica se a autenticação e a geração do token ocorreram na ordem esperada
            verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(tokenUtil, times(1)).generateToken(mockUser);
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException (411 - UNAUTHORIZED) quando as credenciais forem incorretas")
        void login_WithInvalidCredentials_ShouldThrowUnauthorized() {
            // Arrange
            when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // Act & Assert
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.UNAUTHORIZED)
                    .hasMessageContaining("E-mail ou senha inválidos");

            // Garante que se falhou na autenticação, o gerador de tokens nunca foi acionado
            verify(tokenUtil, never()).generateToken(any());
        }

        @Test
        @DisplayName("Deve lançar ResponseStatusException (500 - INTERNAL_SERVER_ERROR) quando ocorrer um erro inesperado")
        void login_WithUnexpectedError_ShouldThrowInternalServerError() {
            // Arrange - Simula uma quebra inesperada (ex: Banco de dados fora do ar ou NullPointer no Security)
            when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new RuntimeException("Conexão recusada"));

            // Act & Assert
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.INTERNAL_SERVER_ERROR)
                    .hasMessageContaining("Erro ao processar o login: Conexão recusada");

            verify(tokenUtil, never()).generateToken(any());
        }
    }
}