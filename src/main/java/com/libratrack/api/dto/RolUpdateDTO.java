package com.libratrack.api.dto;

import jakarta.validation.constraints.NotNull;

public class RolUpdateDTO {

  @NotNull(message = "Debe especificar el estado de moderador")
  private Boolean esModerador;

  @NotNull(message = "Debe especificar el estado de administrador")
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
