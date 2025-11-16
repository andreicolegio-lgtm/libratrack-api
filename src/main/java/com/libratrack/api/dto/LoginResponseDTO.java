package com.libratrack.api.dto;

public class LoginResponseDTO {

  private String accessToken;

  private String refreshToken;

  private String tipo = "Bearer";

  public LoginResponseDTO(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public String getTipo() {
    return tipo;
  }
}
