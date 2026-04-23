package com.example.active.usuario;

import com.example.active.usuario.dto.UsuarioRequest;
import com.example.active.usuario.dto.UsuarioResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class Controller {
     //registra o que está acontecendo
     private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private Service service;

    @PostMapping
    public ResponseEntity<UsuarioResponse> create (@Valid @RequestBody UsuarioRequest request){
        logger.info("Pos/usuario - Indentificador: {}", request.getEmail());
        UsuarioResponse response = service.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
