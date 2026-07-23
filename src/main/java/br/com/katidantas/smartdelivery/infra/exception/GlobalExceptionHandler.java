package br.com.katidantas.smartdelivery.infra.exception;

import br.com.katidantas.smartdelivery.cliente.ValidacaoClienteException;
import br.com.katidantas.smartdelivery.endereco.CepInvalidoException;
import br.com.katidantas.smartdelivery.restaurante.ValidacaoRestauranteException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(CepInvalidoException.class)
    public ResponseEntity<String> handleCepInvalido(CepInvalidoException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ValidacaoRestauranteException.class)
    public ResponseEntity<String> handleValidacaoRestaurante(ValidacaoRestauranteException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ValidacaoClienteException.class)
    public ResponseEntity<String> handleCpfDuplicado(ValidacaoClienteException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
