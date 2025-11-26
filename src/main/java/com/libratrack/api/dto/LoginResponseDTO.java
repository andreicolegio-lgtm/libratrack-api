package com.libratrack.api.dto;

/** DTO que contiene las credenciales de acceso (tokens) devueltas tras un login exitoso. */
public class LoginResponseDTO {

  private String accessToken;
  private String refreshToken;
  private String tipo = "Bearer";

  public LoginResponseDTO(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

  // Getters y Setters
  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }
}
