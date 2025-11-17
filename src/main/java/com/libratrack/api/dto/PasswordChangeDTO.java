package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordChangeDTO {

  @NotBlank(message = "VALIDATION_PASSWORD_CURRENT_REQUIRED")
  private String contraseñaActual;

  @NotBlank(message = "VALIDATION_PASSWORD_NEW_REQUIRED")
  @Size(min = 8, message = "VALIDATION_PASSWORD_MIN_8")
  private String nuevaContraseña;

  public String getContraseñaActual() {
    return contraseñaActual;
  }

  public void setContraseñaActual(String contraseñaActual) {
    this.contraseñaActual = contraseñaActual;
  }

  public String getNuevaContraseña() {
    return nuevaContraseña;
  }

  public void setNuevaContraseña(String nuevaContraseña) {
    this.nuevaContraseña = nuevaContraseña;
  }
}
