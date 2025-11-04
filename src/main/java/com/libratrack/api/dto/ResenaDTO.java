package com.libratrack.api.dto;

/**
 * DTO para crear o actualizar una Reseña (RF12).
 * El usuario enviará un JSON con estos datos.
 */
public class ResenaDTO {

    // IDs para identificar la relación
    private Long usuarioId;
    private Long elementoId;

    // Datos de la reseña
    private Integer valoracion;
    private String textoResena;

    // --- Getters y Setters ---

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getElementoId() {
        return elementoId;
    }

    public void setElementoId(Long elementoId) {
        this.elementoId = elementoId;
    }

    public Integer getValoracion() {
        return valoracion;
    }

    public void setValoracion(Integer valoracion) {
        this.valoracion = valoracion;
    }

    public String getTextoResena() {
        return textoResena;
    }

    public void setTextoResena(String textoResena) {
        this.textoResena = textoResena;
    }
}