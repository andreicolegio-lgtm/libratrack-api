package com.libratrack.api.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Captura global de excepciones para toda la API. Transforma las excepciones Java en respuestas
 * JSON estandarizadas y traducidas.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @Autowired private MessageSource messageSource;

  // --- UTILIDADES ---

  private String getLocalizedMessage(String key, WebRequest request) {
    try {
      return messageSource.getMessage(key, null, request.getLocale());
    } catch (Exception e) {
      // Si falla la traducción (clave no existe), devolvemos la clave original para depuración
      return key;
    }
  }

  private Map<String, Object> buildErrorBody(
      HttpStatus status, String errorKey, String message, String path) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", new Date());
    body.put("status", status.value());
    body.put("error", errorKey);
    body.put("message", message);
    body.put("path", path.replace("uri=", ""));
    return body;
  }

  // --- MANEJADORES ESPECÍFICOS ---

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
      ResourceNotFoundException ex, WebRequest request) {
    String message = getLocalizedMessage(ex.getMessage(), request);
    return new ResponseEntity<>(
        buildErrorBody(
            HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", message, request.getDescription(false)),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<Map<String, Object>> handleConflictException(
      ConflictException ex, WebRequest request) {
    String message = getLocalizedMessage(ex.getMessage(), request);
    return new ResponseEntity<>(
        buildErrorBody(HttpStatus.CONFLICT, "CONFLICT", message, request.getDescription(false)),
        HttpStatus.CONFLICT);
  }

  @ExceptionHandler({AccessDeniedException.class, TokenRefreshException.class})
  public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
      RuntimeException ex, WebRequest request) {
    logger.warn("Acceso denegado: {}", ex.getMessage());
    String messageKey =
        ex instanceof TokenRefreshException ? ex.getMessage() : "exception.auth.access_denied";
    String message = getLocalizedMessage(messageKey, request);

    return new ResponseEntity<>(
        buildErrorBody(
            HttpStatus.FORBIDDEN, "ACCESS_DENIED", message, request.getDescription(false)),
        HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
  public ResponseEntity<Map<String, Object>> handleAuthExceptions(
      RuntimeException ex, WebRequest request) {
    String message = getLocalizedMessage("exception.auth.credentials_invalid", request);
    return new ResponseEntity<>(
        buildErrorBody(
            HttpStatus.UNAUTHORIZED, "AUTH_FAILED", message, request.getDescription(false)),
        HttpStatus.UNAUTHORIZED);
  }

  /** Maneja errores de validación de DTOs (@Valid). Devuelve un mapa detallado campo -> error. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {

    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            error -> {
              String message = getLocalizedMessage(error.getDefaultMessage(), request);
              fieldErrors.put(error.getField(), message);
            });

    Map<String, Object> body =
        buildErrorBody(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            "Error de validación en los datos enviados",
            request.getDescription(false));
    body.put("fieldErrors", fieldErrors);

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {

    Map<String, String> violations = new HashMap<>();
    ex.getConstraintViolations()
        .forEach(
            violation -> {
              String field = violation.getPropertyPath().toString();
              String message = getLocalizedMessage(violation.getMessage(), request);
              violations.put(field, message);
            });

    Map<String, Object> body =
        buildErrorBody(
            HttpStatus.BAD_REQUEST,
            "CONSTRAINT_VIOLATION",
            "Error de integridad de datos",
            request.getDescription(false));
    body.put("violations", violations);

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    String message = getLocalizedMessage(ex.getMessage(), request);
    return new ResponseEntity<>(
        buildErrorBody(
            HttpStatus.BAD_REQUEST, "ILLEGAL_ARGUMENT", message, request.getDescription(false)),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGlobalException(
      Exception ex, WebRequest request) {
    logger.error("Error no controlado: ", ex);
    String message = getLocalizedMessage("exception.internal_server_error", request);
    return new ResponseEntity<>(
        buildErrorBody(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            message,
            request.getDescription(false)),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
