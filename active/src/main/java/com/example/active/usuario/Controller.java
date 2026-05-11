package com.example.active.usuario;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class Controller {
     //registra o que está acontecendo
     private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private UsuarioService service;

    @PostMapping
    public ResponseEntity<UsuarioResponse> create (@Valid @RequestBody UsuarioRequest request){
        logger.info("Post/usuario - Indentificador: {}", request.getEmail());
        UsuarioResponse response = service.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> getById(@PathVariable Long id) {
        logger.info("Get/usuario - Identificador: {}", id);
        UsuarioResponse response = service.buscarporId(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioResponse> deleteById(@PathVariable Long id) {
        logger.info("Delete/usuario - Identificador: {}", +id);
        try {
            service.deletarPorId(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> updateById(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
        logger.info("Put/usuario - Identificador: {}, id");
        UsuarioResponse response = service.atualizarPorId(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
