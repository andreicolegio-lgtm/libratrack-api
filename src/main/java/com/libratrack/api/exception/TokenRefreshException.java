package com.libratrack.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción específica para fallos durante el proceso de renovación de Access Token (ej. Refresh
 * Token expirado o no encontrado en BD). Se mapea a HTTP 403 Forbidden.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class TokenRefreshException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TokenRefreshException(String token, String messageKey) {
    super(messageKey);
  }
}
