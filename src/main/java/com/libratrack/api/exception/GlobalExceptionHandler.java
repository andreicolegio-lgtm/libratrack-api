package com.libratrack.api.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @Autowired
  private MessageSource messageSource;

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
    body.put("error", "E_ACCESS_DENIED");
    body.put("message", messageSource.getMessage("error.access_denied", null, request.getLocale()));
    body.put("path", request.getDescription(false).substring(4));

    return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
  }

  private String getLocalizedMessage(String key, WebRequest request) {
    try {
      return messageSource.getMessage(key, null, request.getLocale());
    } catch (Exception e) {
      logger.warn("Localization failed for key: {}. Using default message.", key);
      return key;
    }
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
      ResourceNotFoundException ex, WebRequest request) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", new Date());
    body.put("status", HttpStatus.NOT_FOUND.value());
    body.put("error", "RESOURCE_NOT_FOUND");
    body.put("message", getLocalizedMessage("error.resource_not_found", request));
    body.put("path", request.getDescription(false).substring(4));

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<Map<String, Object>> handleConflictException(
      ConflictException ex, WebRequest request) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", new Date());
    body.put("status", HttpStatus.CONFLICT.value());
    body.put("error", ex.getMessage());
    body.put("path", request.getDescription(false).substring(4));

    return new ResponseEntity<>(body, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {

    String firstErrorKey = "VALIDATION_ERROR";
    if (ex.getBindingResult().hasFieldErrors()) {
      FieldError firstError = ex.getBindingResult().getFieldErrors().get(0);
      if (firstError.getDefaultMessage() != null) {
        firstErrorKey = firstError.getDefaultMessage();
      }
    }

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", new Date());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", firstErrorKey);
    body.put("message", "Validation Error");
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

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {
    logger.warn("Validation failed: {}", ex.getMessage());
    logger.warn("Constraint violations:");
    ex.getConstraintViolations().forEach(violation ->
        logger.warn("Property: {} - Message: {} - Invalid Value: {}",
            violation.getPropertyPath(),
            violation.getMessage(),
            violation.getInvalidValue()));

    List<String> violations =
        ex.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.toList());

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", new Date());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "VALIDATION_FAILED");
    body.put("violations", violations);
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
    body.put("message", getLocalizedMessage("error.internal_server", request));
    body.put("path", request.getDescription(false).substring(4));

    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
