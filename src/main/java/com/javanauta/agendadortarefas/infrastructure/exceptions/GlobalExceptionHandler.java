package com.javanauta.agendadortarefas.infrastructure.exceptions;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private Map<String, Object> buildError(String message, HttpStatus status, String path) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", message);
        if (path != null) {
            error.put("path", path);
        }
        return error;
    }

    private Map<String, Object> buildError(String message, HttpStatus status) {
        return buildError(message, status, null);
    }

    // ✅ Tarefa não encontrada
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        log.warn("Recurso não encontrado: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI()));
    }

    // ✅ Token JWT inválido/expirado
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, Object>> handleJwtError(
            JwtException ex, HttpServletRequest request) {

        log.warn("Erro JWT: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError("Token JWT inválido ou expirado", HttpStatus.UNAUTHORIZED, request.getRequestURI()));
    }

    // ✅ Erro do MongoDB (conexão, timeout, etc)
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleMongoError(
            DataAccessException ex, HttpServletRequest request) {

        log.error("Erro no MongoDB: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(buildError("Erro ao acessar o banco de dados. Tente novamente.",
                        HttpStatus.SERVICE_UNAVAILABLE, request.getRequestURI()));
    }

    // ✅ Validação de campos (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<String> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();

        log.warn("Erros de validação: {}", erros);

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Erro de Validação");
        error.put("message", "Dados inválidos");
        error.put("errors", erros);
        error.put("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ✅ Erro genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(
            Exception ex, HttpServletRequest request) {

        log.error("Erro não tratado: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError("Erro inesperado: " + ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI()));
    }
}