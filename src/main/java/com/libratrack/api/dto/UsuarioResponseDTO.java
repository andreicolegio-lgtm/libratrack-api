package com.libratrack.api.dto;

import com.libratrack.api.entity.Usuario;

/**
 * DTO seguro para compartir información de usuarios públicamente o en administración. Excluye datos
 * sensibles como contraseñas.
 */
public class UsuarioResponseDTO {

  private Long id;
  private String username;
  private String email;
  private String fotoPerfilUrl;
  private boolean esModerador;
  private boolean esAdministrador;

  public UsuarioResponseDTO(Usuario usuario) {
    this.id = usuario.getId();
    this.username = usuario.getUsername();
    this.email = usuario.getEmail();
    this.fotoPerfilUrl = usuario.getFotoPerfilUrl();
    this.esAdministrador = usuario.esAdmin();
    this.esModerador = usuario.esMod();
  }

  // Getters
  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getFotoPerfilUrl() {
    return fotoPerfilUrl;
  }

  public boolean isEsModerador() {
    return esModerador;
  }

  public boolean isEsAdministrador() {
    return esAdministrador;
  }
}
