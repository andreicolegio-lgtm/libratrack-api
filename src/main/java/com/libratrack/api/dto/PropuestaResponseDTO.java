package com.libratrack.api.dto;

import com.libratrack.api.entity.PropuestaElemento; 
import com.libratrack.api.model.EstadoPropuesta; 
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la RESPUESTA al ENVIAR una Propuesta.
 * --- ¡ACTUALIZADO (Sprint 2 / V2)! ---
 */
public class PropuestaResponseDTO {

    // ... (Campos básicos: id, titulo, desc, etc. sin cambios) ...
    private Long id;
    private String tituloSugerido;
    private String descripcionSugerida;
    private String tipoSugerido;
    private String generosSugeridos;
    private EstadoPropuesta estadoPropuesta;
    private String comentariosRevision;
    private LocalDateTime fechaPropuesta;

    // --- ¡CAMPOS DE PROGRESO REFACTORIZADOS! (Petición b, c, d) ---
    private String episodiosPorTemporada;
    private Integer totalUnidades;
    private Integer totalCapitulosLibro;
    private Integer totalPaginasLibro;
    // (Ya no necesitamos enviar 'esUnidadUnica' porque el 'Tipo' lo define)

    // --- Relaciones Aplanadas ---
    private String proponenteUsername;
    private String revisorUsername;
    
    // --- Constructor de Mapeo ---
    public PropuestaResponseDTO(PropuestaElemento p) {
        this.id = p.getId();
        this.tituloSugerido = p.getTituloSugerido();
        this.descripcionSugerida = p.getDescripcionSugerida();
        this.tipoSugerido = p.getTipoSugerido();
        this.generosSugeridos = p.getGenerosSugeridos();
        this.estadoPropuesta = p.getEstadoPropuesta();
        this.comentariosRevision = p.getComentariosRevision();
        this.fechaPropuesta = p.getFechaPropuesta();

        // --- ¡NUEVO MAPEO DE PROGRESO! (Refactorizado) ---
        this.episodiosPorTemporada = p.getEpisodiosPorTemporada();
        this.totalUnidades = p.getTotalUnidades();
        this.totalCapitulosLibro = p.getTotalCapitulosLibro();
        this.totalPaginasLibro = p.getTotalPaginasLibro();

        // Mapeo Seguro (Aplanado) de Relaciones LAZY
        this.proponenteUsername = p.getProponente().getUsername();
        if (p.getRevisor() != null) {
            this.revisorUsername = p.getRevisor().getUsername();
        } else {
            this.revisorUsername = null; 
        }
    }

    
    // --- Getters ---
    // (Jackson los necesita para construir el JSON)
    public Long getId() { return id; }
    public String getTituloSugerido() { return tituloSugerido; }
    public String getDescripcionSugerida() { return descripcionSugerida; }
    public String getTipoSugerido() { return tipoSugerido; }
    public String getGenerosSugeridos() { return generosSugeridos; }
    public EstadoPropuesta getEstadoPropuesta() { return estadoPropuesta; }
    public String getComentariosRevision() { return comentariosRevision; }
    public LocalDateTime getFechaPropuesta() { return fechaPropuesta; }
    public String getProponenteUsername() { return proponenteUsername; }
    public String getRevisorUsername() { return revisorUsername; }
    
    // Getters de Progreso
    public String getEpisodiosPorTemporada() { return episodiosPorTemporada; }
    public Integer getTotalUnidades() { return totalUnidades; }
    public Integer getTotalCapitulosLibro() { return totalCapitulosLibro; }
    public Integer getTotalPaginasLibro() { return totalPaginasLibro; }
}