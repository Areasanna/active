package com.example.active.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    //Erros de validação  - HTTP 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> validationError(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        StandardError erro = new StandardError(HttpStatus.BAD_REQUEST.value(), mensagem, LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(erro);
    }


    //Recurso não encontrado - HTTP 404
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<StandardError> handleNotFound(EntityNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EmailCadastrado.class)
    public ResponseEntity<StandardError> handleConflict(EmailCadastrado ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    //Erro Genérico (Fallback) - 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleGeneric(Exception ex) {
        ex.printStackTrace();

        StandardError erro = new StandardError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                " Erro interno inesperado no servidor.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    private ResponseEntity<StandardError> buildResponse(HttpStatus status, String message) {
        StandardError erro = new StandardError(
                status.value(),
                message,
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(org.springframework.dao.InvalidDataAccessApiUsageException.class)
    public ResponseEntity<StandardError> handleInvalidDataAccess(org.springframework.dao.InvalidDataAccessApiUsageException ex) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Erro na sintaxe da requisição ou campo de ordenação inválido."
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<StandardError> handleRuntime(RuntimeException ex) {
        if (ex.getClass().getSimpleName().equals("PropertyReferenceException")) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Campo de ordenação não existe no banco de dados.");
        }
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor.");

    }
}

