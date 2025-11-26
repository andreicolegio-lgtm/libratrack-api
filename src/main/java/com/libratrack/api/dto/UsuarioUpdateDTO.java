package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO para que un usuario actualice su propia información básica de perfil. */
public class UsuarioUpdateDTO {

  @NotBlank(message = "{validation.usuario.username.required}")
  @Size(min = 4, max = 50, message = "{validation.usuario.username.size}")
  private String username;

  // Getters y Setters
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
