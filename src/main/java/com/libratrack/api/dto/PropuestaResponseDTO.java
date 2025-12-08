package com.libratrack.api.dto;

import com.libratrack.api.entity.PropuestaElemento;
import com.libratrack.api.model.EstadoPropuesta;
import java.time.LocalDateTime;

/**
 * DTO para mostrar los detalles de una propuesta en el panel de moderación o historial de usuario.
 */
public class PropuestaResponseDTO {

  private Long id;
  private String tituloSugerido;
  private String descripcionSugerida;
  private String tipoSugerido;
  private String generosSugeridos;
  private EstadoPropuesta estadoPropuesta;
  private String comentariosRevision;
  private LocalDateTime fechaPropuesta;

  private String episodiosPorTemporada;
  private Integer totalUnidades;
  private Integer totalCapitulosLibro;
  private Integer totalPaginasLibro;

  private String proponenteUsername;
  private String revisorUsername;
  private String duracion;

  private String proponenteEmail;
  private String revisorEmail;

  private Long elementoCreadoId; // ID del elemento creado a partir de la propuesta

  public PropuestaResponseDTO(PropuestaElemento p) {
    this.id = p.getId();
    this.tituloSugerido = p.getTituloSugerido();
    this.descripcionSugerida = p.getDescripcionSugerida();
    this.tipoSugerido = p.getTipoSugerido();
    this.generosSugeridos = p.getGenerosSugeridos();
    this.estadoPropuesta = p.getEstadoPropuesta();
    this.comentariosRevision = p.getComentariosRevision();
    this.fechaPropuesta = p.getFechaPropuesta();

    this.episodiosPorTemporada = p.getEpisodiosPorTemporada();
    this.totalUnidades = p.getTotalUnidades();
    this.totalCapitulosLibro = p.getTotalCapitulosLibro();
    this.totalPaginasLibro = p.getTotalPaginasLibro();
    this.duracion = p.getDuracion();

    if (p.getProponente() != null) {
      this.proponenteUsername = p.getProponente().getUsername();
      this.proponenteEmail = p.getProponente().getEmail();
    } else {
      this.proponenteUsername = null;
      this.proponenteEmail = null;
    }

    if (p.getRevisor() != null) {
      this.revisorUsername = p.getRevisor().getUsername();
      this.revisorEmail = p.getRevisor().getEmail();
    } else {
      this.revisorUsername = null;
      this.revisorEmail = null;
    }

    if (p.getElementoCreado() != null) {
      this.elementoCreadoId = p.getElementoCreado().getId();
    }
  }

  // Getters
  public Long getId() {
    return id;
  }

  public String getTituloSugerido() {
    return tituloSugerido;
  }

  public String getDescripcionSugerida() {
    return descripcionSugerida;
  }

  public String getTipoSugerido() {
    return tipoSugerido;
  }

  public String getGenerosSugeridos() {
    return generosSugeridos;
  }

  public EstadoPropuesta getEstadoPropuesta() {
    return estadoPropuesta;
  }

  public String getComentariosRevision() {
    return comentariosRevision;
  }

  public LocalDateTime getFechaPropuesta() {
    return fechaPropuesta;
  }

  public String getProponenteUsername() {
    return proponenteUsername;
  }

  public String getRevisorUsername() {
    return revisorUsername;
  }

  public String getEpisodiosPorTemporada() {
    return episodiosPorTemporada;
  }

  public Integer getTotalUnidades() {
    return totalUnidades;
  }

  public Integer getTotalCapitulosLibro() {
    return totalCapitulosLibro;
  }

  public Integer getTotalPaginasLibro() {
    return totalPaginasLibro;
  }

  public String getDuracion() {
    return duracion;
  }

  public String getProponenteEmail() {
    return proponenteEmail;
  }

  public void setProponenteEmail(String proponenteEmail) {
    this.proponenteEmail = proponenteEmail;
  }

  public String getRevisorEmail() {
    return revisorEmail;
  }

  public void setRevisorEmail(String revisorEmail) {
    this.revisorEmail = revisorEmail;
  }

  public Long getElementoCreadoId() {
    return elementoCreadoId;
  }
}
