package com.example.active.config;

import com.example.active.service.CustomUserDetailsService;
import com.example.active.model.User;
import com.example.active.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User mockUser;
    private final String email = "ana.cunha@email.com";

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail(email);
    }

    @Nested
    @DisplayName("Testes do método loadUserByUsername")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Deve retornar UserDetails com sucesso quando o e-mail existir no banco")
        void loadUserByUsername_WithExistingEmail_ShouldReturnUserDetails() {
            // Arrange
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

            // Act
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            // Assert
            assertThat(userDetails).isNotNull();
            assertThat(userDetails.getUsername()).isEqualTo(email);

            verify(userRepository, times(1)).findByEmail(email);
        }

        @Test
        @DisplayName("Deve lançar UsernameNotFoundException quando o e-mail não for localizado")
        void loadUserByUsername_WithNonExistingEmail_ShouldThrowUsernameNotFoundException() {
            // Arrange
            String emailInexistente = "nao_existe@email.com";
            when(userRepository.findByEmail(emailInexistente)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(emailInexistente))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("Usuário não encontrado: " + emailInexistente);

            verify(userRepository, times(1)).findByEmail(emailInexistente);
        }
    }
}