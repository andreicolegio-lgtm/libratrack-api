package com.libratrack.api.dto;

import jakarta.validation.constraints.NotNull;

public class RolUpdateDTO {

  @NotNull(message = "VALIDATION_MOD_STATUS_REQUIRED")
  private Boolean esModerador;

  @NotNull(message = "VALIDATION_ADMIN_STATUS_REQUIRED")
  private Boolean esAdministrador;

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
