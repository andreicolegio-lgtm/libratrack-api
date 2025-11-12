package com.libratrack.api.dto;

import com.libratrack.api.entity.Usuario;

/**
 * DTO para enviar la información del perfil del usuario al cliente (Flutter).
 * --- ¡ACTUALIZADO (Sprint 4 - Corrección)! ---
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
        
        // --- ¡LÓGICA CORREGIDA! ---
        // (Debe coincidir con la lógica de UserDetailsServiceImpl)
        
        // 1. Comprobamos si es Administrador
        this.esAdministrador = usuario.getEsAdministrador() != null && usuario.getEsAdministrador();
        
        // 2. Es Moderador SI es Admin (Petición 16) O si el flag es true
        if (this.esAdministrador) {
            this.esModerador = true;
        } else {
            this.esModerador = usuario.getEsModerador() != null && usuario.getEsModerador();
        }
    }

    // --- Getters ---
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFotoPerfilUrl() { return fotoPerfilUrl; }

    // (Corregidos en 20251108-A61)
    public boolean isEsModerador() { return esModerador; }
    public boolean isEsAdministrador() { return esAdministrador; }
}