package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;

public class GoogleTokenDTO {

  @NotBlank(message = "El token de Google no puede estar vacío")
  private String token;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
