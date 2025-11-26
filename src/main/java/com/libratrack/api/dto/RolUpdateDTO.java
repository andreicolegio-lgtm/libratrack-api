package com.libratrack.api.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para la gestión de permisos y roles de usuario (Admin/Moderador). Utilizado exclusivamente
 * por administradores.
 */
public class RolUpdateDTO {

  @NotNull(message = "{validation.rol.moderador.required}")
  private Boolean esModerador;

  @NotNull(message = "{validation.rol.admin.required}")
  private Boolean esAdministrador;

  // Getters y Setters
  public Boolean getEsModerador() {
    return esModerador;
  }

  public void setEsModerador(Boolean esModerador) {
    this.esModerador = esModerador;
  }

  public Boolean getEsAdministrador() {
    return esAdministrador;
  }

  public void setEsAdministrador(Boolean esAdministrador) {
    this.esAdministrador = esAdministrador;
  }
}
