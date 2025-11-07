package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para RECIBIR una propuesta de Elemento del cliente (RF13).
 */
public class PropuestaRequestDTO {

    // --- Campos de Elemento ---

    @NotBlank(message = "El título sugerido no puede estar vacío")
    @Size(max = 255, message = "El título sugerido no puede exceder los 255 caracteres")
    private String tituloSugerido;

    @NotBlank(message = "La descripción sugerida no puede estar vacía")
    @Size(max = 5000, message = "La descripción sugerida no puede exceder los 5000 caracteres")
    private String descripcionSugerida;
    
    // --- Campos de Tipo/Género (EXPECTED BY SERVICE) ---
    
    @NotBlank(message = "El tipo sugerido no puede ser nulo")
    private String tipoSugerido; // <-- ¡ESTO SOLUCIONA EL ERROR!
    
    @NotBlank(message = "Los géneros sugeridos no pueden ser nulos")
    private String generosSugeridos; // <-- ¡ESTO SOLUCIONA EL ERROR!

    // --- Campo de Multimedia ---
    private String imagenPortadaUrl; 
    
    // --- Getters y Setters ---

    public String getTituloSugerido() {
        return tituloSugerido;
    }

    public void setTituloSugerido(String tituloSugerido) {
        this.tituloSugerido = tituloSugerido;
    }

    public String getDescripcionSugerida() {
        return descripcionSugerida;
    }

    public void setDescripcionSugerida(String descripcionSugerida) {
        this.descripcionSugerida = descripcionSugerida;
    }

    // --- Getters y Setters que solucionan el error ---
    public String getTipoSugerido() {
        return tipoSugerido;
    }

    public void setTipoSugerido(String tipoSugerido) {
        this.tipoSugerido = tipoSugerido;
    }

    public String getGenerosSugeridos() {
        return generosSugeridos;
    }

    public void setGenerosSugeridos(String generosSugeridos) {
        this.generosSugeridos = generosSugeridos;
    }
    
    // --- Getter y Setter para la URL de la imagen ---
    
    public String getImagenPortadaUrl() {
        return imagenPortadaUrl;
    }

    public void setImagenPortadaUrl(String imagenPortadaUrl) {
        this.imagenPortadaUrl = imagenPortadaUrl;
    }
}