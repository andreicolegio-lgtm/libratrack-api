package com.libratrack.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando una operación entra en conflicto con el estado actual del recurso.
 *
 * <p>Ejemplo común: Intentar registrar un usuario con un email que ya existe (HTTP 409 Conflict).
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ConflictException(String mensaje) {
    super(mensaje);
  }
}
