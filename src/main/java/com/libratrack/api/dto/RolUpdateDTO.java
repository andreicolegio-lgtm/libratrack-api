package com.libratrack.api.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para recibir la petición de actualizar los roles
 * de un usuario (Petición 14).
 */
public class RolUpdateDTO {

    @NotNull(message = "Debe especificar el estado de moderador")
    private Boolean esModerador;

    @NotNull(message = "Debe especificar el estado de administrador")
    private Boolean esAdministrador;

    // --- Getters y Setters ---
    
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