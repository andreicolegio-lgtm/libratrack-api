package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;

/** DTO para recibir el token de identidad (ID Token) de Google desde el cliente móvil. */
public class GoogleTokenDTO {

  @NotBlank(message = "{validation.google.token.required}")
  private String token;

  // Getters y Setters
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
