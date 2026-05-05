package com.example.active;

import com.example.active.usuario.EmailCadastrado;
import com.example.active.usuario.StandardError;
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
    public ResponseEntity<StandardError> ValidacaoErro(MethodArgumentNotValidException ex){
      //junção de todos os erros de campo em uma única mensagem
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        StandardError erro = new StandardError(HttpStatus.BAD_REQUEST.value(), mensagem, LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(erro);
    }

    //Recurso não encontrado - HTTP 404
    @ExceptionHandler(EmailCadastrado.class)
    public  ResponseEntity<StandardError> handleNotFound(EmailCadastrado ex){
        StandardError erro = new StandardError(
                HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now()
        );
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    //Email já cadastrado - 409
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<StandardError> handleConflict(RuntimeException ex) {
        StandardError erro = new StandardError(HttpStatus.CONFLICT.value(),
                ex.getMessage(), LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }
    //Erro Genérico (Fallback) - 500

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> ErroGenerico(Exception ex){
        StandardError erro = new StandardError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                " Erro interno inesperado no servidor.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
