package com.example.active.service;

import com.example.active.exception.EmailCadastrado;
import com.example.active.dto.UserRequest;
import com.example.active.dto.UserResponse;
import com.example.active.model.User;
import com.example.active.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    //nunca armazenar senhas em texto puro
    private final BCryptPasswordEncoder passwordEncoder;


    @Transactional(readOnly = true)
    public Page<UserResponse> list(Specification<User> spec, Pageable pageable) {
        Page<User> page = userRepository.findAll(spec, pageable);

        if (page.isEmpty()) {
            throw new EntityNotFoundException("Nenhum usuário encontrado para os parâmetros fornecidos.");
        }

        return page.map(this::toResponse);

    }

    @Transactional
    public UserResponse cadastrar(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailCadastrado("E-mail já cadastrado");
        }
        if (!request.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
            throw new EntityNotFoundException("Senha deve conter letras e números e no mínimo 8 caracters");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setHeight(request.getHeight());
        user.setWeight(request.getWeight());
        user.setTrainingLevel(request.getTrainingLevel());

        user.setDateOfBirth(request.getDateOfBirth());

        if(user.getAge() < 14){
            throw new IllegalArgumentException("Cadastro rejeitado: para fazer o cadastro o usuário tem que ter no mínimo 14 anos");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);
        userRepository.flush();

        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse buscarporId(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + id));
        return toResponse(user);
    }

    @Transactional
    public void deletarPorId(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id:" + id));
        userRepository.delete(user);
    }

    @Transactional
    public UserResponse atualizarPorId(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + id));

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setHeight(request.getHeight());
        user.setWeight(request.getWeight());
        user.setTrainingLevel(request.getTrainingLevel());

        if(user.getAge() < 14){
            throw new IllegalArgumentException("Atualização não concluída: O usuário tem que ter no mínimo 14 anos");
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (!request.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
                throw new RuntimeException("Senha deve conter letras e números e no mínimo 8 caracteres");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user = userRepository.save(user);

        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getTrainingLevel(),
                user.getAge(),
                user.getWeight(),
                user.getHeight(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}

