package com.example.active.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Erros de Validação (Bean Validation @NotBlank, etc)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> validationError(MethodArgumentNotValidException ex) {
        // Formatação mais limpa: "campo: mensagem, campo2: mensagem"
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, "Erro de validação: " + mensagem);
    }

    // 2. Erros de Negócio Específicos
    @ExceptionHandler(EmailCadastrado.class)
    public ResponseEntity<StandardError> handleConflict(EmailCadastrado ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // 3. Recurso não encontrado (JPA e Service)
    @ExceptionHandler({EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<StandardError> handleNotFound(Exception ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 4. Captura o que você usou nos Services: ResponseStatusException
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<StandardError> handleResponseStatus(org.springframework.web.server.ResponseStatusException ex) {
        return buildResponse(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason());
    }

    @ExceptionHandler(org.springframework.dao.InvalidDataAccessApiUsageException.class)

    public ResponseEntity<StandardError> handleInvalidDataAccess(org.springframework.dao.InvalidDataAccessApiUsageException ex) {

        return buildResponse(

                HttpStatus.BAD_REQUEST,

                "Erro na sintaxe da requisição ou campo de ordenação inválido."

        );

    }

    // 6. Fallback para erros inesperados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleGeneric(Exception ex) {
        // Logamos o erro completo no console para o desenvolvedor
        ex.printStackTrace();
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Um erro interno inesperado ocorreu.");
    }

    private ResponseEntity<StandardError> buildResponse(HttpStatus status, String message) {
        StandardError erro = new StandardError(
                status.value(),
                message,
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(erro);
    }
}


