package com.libratrack.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false, length = 50)
  @NotBlank(message = "VALIDATION_USERNAME_REQUIRED")
  @Size(min = 4, max = 50, message = "VALIDATION_USERNAME_LENGTH_4_50")
  private String username;

  @Column(unique = true, nullable = false, length = 100)
  @NotBlank(message = "VALIDATION_EMAIL_REQUIRED")
  @Email(message = "VALIDATION_EMAIL_INVALID")
  private String email;

  @Column(nullable = false)
  @NotBlank(message = "VALIDATION_PASSWORD_REQUIRED")
  private String password;

  @Column(nullable = false)
  private Boolean esModerador = false;

  @Column(nullable = false)
  private Boolean esAdministrador = false;

  @Column(length = 255)
  private String fotoPerfilUrl;

  public Usuario() {}

  public Usuario(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Boolean getEsModerador() {
    return esModerador;
  }

  public void setEsModerador(Boolean esModerador) {
    this.esModerador = esModerador;
  }

  public String getFotoPerfilUrl() {
    return fotoPerfilUrl;
  }

  public void setFotoPerfilUrl(String fotoPerfilUrl) {
    this.fotoPerfilUrl = fotoPerfilUrl;
  }

  public Boolean getEsAdministrador() {
    return esAdministrador;
  }

  public void setEsAdministrador(Boolean esAdministrador) {
    this.esAdministrador = esAdministrador;
  }

  @Transient
  public boolean esAdmin() {
    return this.esAdministrador != null && this.esAdministrador;
  }

  @Transient
  public boolean esMod() {
    if (esAdmin()) {
      return true;
    }
    return this.esModerador != null && this.esModerador;
  }
}
