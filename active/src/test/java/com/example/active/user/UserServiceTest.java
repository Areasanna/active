package com.example.active.user;

import com.example.active.exception.EmailCadastrado;
import com.example.active.service.UserService;
import com.example.active.dto.UserRequest;
import com.example.active.dto.UserResponse;
import com.example.active.model.User;
import com.example.active.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        // Inicializa dados padrão para reaproveitar nos testes
        user = new User();
        user.setId(1L);
        user.setName("Laura");
        user.setEmail("laura@email.com");
        user.setPassword("encodedPassword123");

        userRequest = new UserRequest();
        userRequest.setName("Laura");
        userRequest.setEmail("laura@email.com");
        userRequest.setPassword("Senha1234");
    }

    @Nested
    class ListagemTests {
        @Test
        void list_DeveRetornarPaginaDeUserResponse_QuandoExistiremUsuarios() {
            Pageable pageable = PageRequest.of(0, 10);
            Specification<User> spec = mock(Specification.class);
            Page<User> page = new PageImpl<>(List.of(user));

            when(userRepository.findAll(spec, pageable)).thenReturn(page);

            Page<UserResponse> resultado = userService.list(spec, pageable);

            assertThat(resultado).isNotEmpty();
            assertThat(resultado.getContent().get(0).email()).isEqualTo(user.getEmail());
        }

        @Test
        void list_DeveLancarentityNotFoundException_QuandoPaginaEstiverVazia() {
            Pageable pageable = PageRequest.of(0, 10);
            Specification<User> spec = mock(Specification.class);
            Page<User> pageVazia = new PageImpl<>(Collections.emptyList());

            when(userRepository.findAll(spec, pageable)).thenReturn(pageVazia);

            assertThatThrownBy(() -> userService.list(spec, pageable))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Nenhum usuário encontrado para os parâmetros fornecidos.");
        }
    }

    @Nested
    class CadastroTests {
        @Test
        void cadastrar_DeveSalvarUsuario_QuandoDadosForemValidos() {
            when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encodedPassword123");
            when(userRepository.save(any(User.class))).thenReturn(user);

            UserResponse resultado = userService.cadastrar(userRequest);

            assertThat(resultado).isNotNull();
            assertThat(resultado.email()).isEqualTo(userRequest.getEmail());
            verify(userRepository, times(1)).save(any(User.class));
            verify(userRepository, times(1)).flush();
        }

        @Test
        void cadastrar_DeveLancarEmailCadastrado_QuandoEmailJaExistir() {
            when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(true);

            assertThatThrownBy(() -> userService.cadastrar(userRequest))
                    .isInstanceOf(EmailCadastrado.class)
                    .hasMessage("E-mail já cadastrado");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void cadastrar_DeveLancarEntityNotFoundException_QuandoSenhaForFraca() {
            userRequest.setPassword("123");

            when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);

            assertThatThrownBy(() -> userService.cadastrar(userRequest))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Senha deve conter letras e números e no mínimo 8 caracters");

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    class BuscaPorIdTests {
        @Test
        void buscarporId_DeveRetornarUserResponse_QuandoIdExistir() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            UserResponse resultado = userService.buscarporId(1L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(1L);
        }

        @Test
        void buscarporId_DeveLancarEntityNotFoundException_QuandoIdNaoExistir() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.buscarporId(1L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Usuário não encontrado com id: 1");
        }
    }

    @Nested
    class DelecaoTests {
        @Test
        void deletarPorId_DeveDeletar_QuandoIdExistir() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            userService.deletarPorId(1L);

            verify(userRepository, times(1)).delete(user);
        }

        @Test
        void deletarPorId_DeveLancarEntityNotFoundException_QuandoIdNaoExistir() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deletarPorId(1L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Usuário não encontrado com id:1");

            verify(userRepository, never()).delete(any(User.class));
        }
    }

    @Nested
    class AtualizacaoTests {
        @Test
        void atualizarPorId_DeveAtualizarComNovaSenha_QuandoSenhaForValida() {
            userRequest.setPassword("NovaSenha123");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(passwordEncoder.encode("NovaSenha123")).thenReturn("newEncoded123");
            when(userRepository.save(any(User.class))).thenReturn(user);

            UserResponse resultado = userService.atualizarPorId(1L, userRequest);

            assertThat(resultado).isNotNull();
            verify(passwordEncoder, times(1)).encode("NovaSenha123");
            verify(userRepository, times(1)).save(user);
        }

        @Test
        void atualizarPorId_NaoDeveAtualizarSenha_QuandoSenhaForNulaOuVazia() {
            userRequest.setPassword(""); // Vazia
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            userService.atualizarPorId(1L, userRequest);

            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        void atualizarPorId_DeveLancarRuntimeException_QuandoNovaSenhaForFraca() {
            userRequest.setPassword("fraca");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> userService.atualizarPorId(1L, userRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Senha deve conter letras e números e no mínimo 8 caracteres");

            verify(userRepository, never()).save(any(User.class));
        }
    }
}
