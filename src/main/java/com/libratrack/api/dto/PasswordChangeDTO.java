package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO para la solicitud de cambio de contraseña del usuario. */
public class PasswordChangeDTO {

  @NotBlank(message = "{validation.password.current.required}")
  private String contraseñaActual;

  @NotBlank(message = "{validation.password.new.required}")
  @Size(min = 8, message = "{validation.password.min_length}")
  private String nuevaContraseña;

  // Getters y Setters
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
