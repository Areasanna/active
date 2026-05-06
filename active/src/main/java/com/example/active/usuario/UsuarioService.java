package com.example.active.usuario;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    //nunca armazenar senhas em texto puro
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado");
        }
        if (!request.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
            throw new RuntimeException("Senha deve conter letras e números e no mínimo 8 caracters");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setAltura(request.getAltura());
        usuario.setIdade(request.getIdade());
        usuario.setPeso(request.getPeso());
        usuario.setTrainingLevel(request.getTrainingLevel());

        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario = repository.save(usuario);
        repository.flush();

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTrainingLevel(),
                usuario.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarporId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + id));
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTrainingLevel(),
                usuario.getCreatedAt()
        );
    }

    @Transactional
    public void deletarPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id:" + id));
        repository.delete(usuario);
    }

    @Transactional
    public UsuarioResponse atualizarPorId(Long id, UsuarioRequest request) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + id));

        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setAltura(request.getAltura());
        usuario.setIdade(request.getIdade());
        usuario.setPeso(request.getPeso());
        usuario.setTrainingLevel(request.getTrainingLevel());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (!request.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
                throw new RuntimeException("Senha deve conter letras e números e no mínimo 8 caracteres");
            }
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        usuario = repository.save(usuario);

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTrainingLevel(),
                usuario.getCreatedAt()
        );
    }
}

