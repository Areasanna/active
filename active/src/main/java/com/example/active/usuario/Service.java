package com.example.active.usuario;

import com.example.active.usuario.dto.UsuarioRequest;
import com.example.active.usuario.dto.UsuarioResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
public class Service {

    @Autowired
    private UsuarioRepository repository;

    //nunca armazenar senhas em texto puro
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request){
        if(repository.existsByEmail(request.getEmail())){
            throw new RuntimeException("E-mail já cadastrado");
        }
        Usuario usuario = new Usuario();
        usuario.setName(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setAltura(request.getAltura());
        usuario.setIdade(request.getIdade());
        usuario.setPeso(request.getPeso());
        usuario.setTrainingLevel(request.getTrainingLevel());

        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        usuario = repository.save(usuario);

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getName(),
                usuario.getEmail(),
                usuario.getTrainingLevel(),
                usuario.getCreatedAt()
        );
    }
}
