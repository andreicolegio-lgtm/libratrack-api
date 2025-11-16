package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsuarioUpdateDTO {

  @NotBlank(message = "El nombre de usuario no puede estar vacío")
  @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
  private String username;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
