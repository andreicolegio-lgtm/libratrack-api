package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordChangeDTO {

  @NotBlank(message = "La contraseña actual no puede estar vacía")
  private String contraseñaActual;

  @NotBlank(message = "La nueva contraseña no puede estar vacía")
  @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
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
