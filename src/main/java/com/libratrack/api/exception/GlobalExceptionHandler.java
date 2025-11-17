package com.libratrack.api.exception;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
      AccessDeniedException ex, WebRequest request) {
    logger.warn(
        "Acceso denegado: {} (Usuario: {})",
        ex.getMessage(),
        request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "desconocido");

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", new Date());
    body.put("status", HttpStatus.FORBIDDEN.value());
    body.put("error", "FORBIDDEN");
    body.put("message", "No tienes permiso para acceder a este recurso.");
    body.put("path", request.getDescription(false).substring(4));

    return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
      ResourceNotFoundException ex, WebRequest request) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", new Date());
    body.put("status", HttpStatus.NOT_FOUND.value());
    body.put("error", "RESOURCE_NOT_FOUND");
    body.put("details", ex.getMessage());
    body.put("path", request.getDescription(false).substring(4));

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<Map<String, Object>> handleConflictException(
      ConflictException ex, WebRequest request) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", new Date());
    body.put("status", HttpStatus.CONFLICT.value());
    body.put("error", ex.getMessage()); // Already a key
    body.put("path", request.getDescription(false).substring(4));

    return new ResponseEntity<>(body, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", new Date());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "VALIDATION_ERROR");
    body.put("message", "Error de validación");
    body.put("errors", errors);
    body.put("path", request.getDescription(false).substring(4));

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", new Date());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "ILLEGAL_ARGUMENT");
    body.put("details", ex.getMessage());
    body.put("path", request.getDescription(false).substring(4));

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGlobalException(
      Exception ex, WebRequest request) {
    logger.error("Error inesperado: {}", ex.getMessage(), ex);

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", new Date());
    body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    body.put("error", "INTERNAL_SERVER_ERROR");
    body.put("message", "An unexpected error occurred. Please try again later.");
    body.put("path", request.getDescription(false).substring(4));

    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
