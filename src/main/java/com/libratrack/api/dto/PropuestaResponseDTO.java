package com.libratrack.api.dto;

import com.libratrack.api.entity.PropuestaElemento;
import com.libratrack.api.model.EstadoPropuesta;
import java.time.LocalDateTime;

/**
 * DTO para ENVIAR una propuesta al cliente (móvil).
 * Resuelve la LazyInitializationException.
 */
public class PropuestaResponseDTO {

    private Long id;
    private String proponenteUsername;
    private String tituloSugerido;
    private String descripcionSugerida;
    private String tipoSugerido;
    private String generosSugeridos;
    private EstadoPropuesta estadoPropuesta;
    private String revisorUsername;
    private String comentariosRevision;
    private LocalDateTime fechaPropuesta;

    // --- Constructor ---
    public PropuestaResponseDTO(PropuestaElemento p) {
        this.id = p.getId();
        this.proponenteUsername = p.getProponente().getUsername(); // Mapeo seguro
        this.tituloSugerido = p.getTituloSugerido();
        this.descripcionSugerida = p.getDescripcionSugerida();
        this.tipoSugerido = p.getTipoSugerido();
        this.generosSugeridos = p.getGenerosSugeridos();
        this.estadoPropuesta = p.getEstadoPropuesta();
        this.comentariosRevision = p.getComentariosRevision();
        this.fechaPropuesta = p.getFechaPropuesta();
        
        // El revisor puede ser nulo si aún está PENDIENTE
        if (p.getRevisor() != null) {
            this.revisorUsername = p.getRevisor().getUsername();
        }
    }

    // --- Getters ---
    // (Spring los necesita para el JSON)
    public Long getId() { return id; }
    public String getProponenteUsername() { return proponenteUsername; }
    public String getTituloSugerido() { return tituloSugerido; }
    public String getDescripcionSugerida() { return descripcionSugerida; }
    public String getTipoSugerido() { return tipoSugerido; }
    public String getGenerosSugeridos() { return generosSugeridos; }
    public EstadoPropuesta getEstadoPropuesta() { return estadoPropuesta; }
    public String getRevisorUsername() { return revisorUsername; }
    public String getComentariosRevision() { return comentariosRevision; }
    public LocalDateTime getFechaPropuesta() { return fechaPropuesta; }
}