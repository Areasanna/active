package com.example.active.user;

import com.example.active.user.dto.UserRequest;
import com.example.active.user.dto.UserResponse;
import com.example.active.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
     //registra o que está acontecendo
     private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userservice;

    @PostMapping
    public ResponseEntity<UserResponse> create (@Valid @RequestBody UserRequest request){
        logger.info("Post/usuario - Indentificador: {}", request.getEmail());
        UserResponse response = userservice.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> list(@Parameter(hidden = true) @And({@Spec(path = "name", spec = LikeIgnoreCase.class),
            @Spec(path = "email", spec = Equal.class),
            @Spec(path = "active", spec = Equal.class)}) Specification<User> spec, @PageableDefault(size = 10) @ParameterObject Pageable pageable) {

        logger.info("Get/usuarios - Paginação: {} - Filtros: {}", pageable, spec);

        Page<UserResponse> response = userservice.list(spec, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        logger.info("Get/usuario - Identificador: {}", id);
        UserResponse response = userservice.buscarporId(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> deleteById(@PathVariable Long id) {
        logger.info("Delete/usuario - Identificador: {}", +id);
        try {
            userservice.deletarPorId(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateById(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        logger.info("Put/usuario - Identificador: {}", id);
        UserResponse response = userservice.atualizarPorId(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
