package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsuarioUpdateDTO {

  @NotBlank(message = "VALIDATION_USERNAME_REQUIRED")
  @Size(min = 4, max = 50, message = "VALIDATION_USERNAME_LENGTH_4_50")
  private String username;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
