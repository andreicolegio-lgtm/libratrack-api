package com.libratrack.api.entity;

import com.libratrack.api.model.EstadoPropuesta;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "propuestas_elementos")
public class PropuestaElemento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "proponente_id", nullable = false)
  private Usuario proponente;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "revisor_id")
  private Usuario revisor;

  @Column(nullable = false)
  @NotBlank(message = "El título no puede estar vacío")
  @Size(max = 255)
  private String tituloSugerido;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String descripcionSugerida;

  @Size(max = 100)
  private String tipoSugerido;

  @Size(max = 255)
  private String generosSugeridos;

  @Column(length = 255)
  private String urlImagen;

  @Column(length = 255)
  private String episodiosPorTemporada;

  private Integer totalUnidades;
  private Integer totalCapitulosLibro;
  private Integer totalPaginasLibro;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoPropuesta estadoPropuesta = EstadoPropuesta.PENDIENTE;

  @Size(max = 500)
  private String comentariosRevision;

  @Column(nullable = false, updatable = false)
  private LocalDateTime fechaPropuesta;

  @PrePersist
  protected void onCrear() {
    this.fechaPropuesta = LocalDateTime.now();
  }

  public PropuestaElemento() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getProponente() {
    return proponente;
  }

  public void setProponente(Usuario proponente) {
    this.proponente = proponente;
  }

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

  public EstadoPropuesta getEstadoPropuesta() {
    return estadoPropuesta;
  }

  public void setEstadoPropuesta(EstadoPropuesta estadoPropuesta) {
    this.estadoPropuesta = estadoPropuesta;
  }

  public Usuario getRevisor() {
    return revisor;
  }

  public void setRevisor(Usuario revisor) {
    this.revisor = revisor;
  }

  public String getComentariosRevision() {
    return comentariosRevision;
  }

  public void setComentariosRevision(String comentariosRevision) {
    this.comentariosRevision = comentariosRevision;
  }

  public LocalDateTime getFechaPropuesta() {
    return fechaPropuesta;
  }

  public void setFechaPropuesta(LocalDateTime fechaPropuesta) {
    this.fechaPropuesta = fechaPropuesta;
  }

  public String getUrlImagen() {
    return urlImagen;
  }

  public void setUrlImagen(String urlImagen) {
    this.urlImagen = urlImagen;
  }

  public String getEpisodiosPorTemporada() {
    return episodiosPorTemporada;
  }

  public void setEpisodiosPorTemporada(String episodiosPorTemporada) {
    this.episodiosPorTemporada = episodiosPorTemporada;
  }

  public Integer getTotalUnidades() {
    return totalUnidades;
  }

  public void setTotalUnidades(Integer totalUnidades) {
    this.totalUnidades = totalUnidades;
  }

  public Integer getTotalCapitulosLibro() {
    return totalCapitulosLibro;
  }

  public void setTotalCapitulosLibro(Integer totalCapitulosLibro) {
    this.totalCapitulosLibro = totalCapitulosLibro;
  }

  public Integer getTotalPaginasLibro() {
    return totalPaginasLibro;
  }

  public void setTotalPaginasLibro(Integer totalPaginasLibro) {
    this.totalPaginasLibro = totalPaginasLibro;
  }
}
