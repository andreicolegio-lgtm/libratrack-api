package com.libratrack.api.entity;

import com.libratrack.api.model.EstadoPersonal;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "catalogo_personal")
public class CatalogoPersonal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "elemento_id", nullable = false)
  private Elemento elemento;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private EstadoPersonal estadoPersonal;

  @Column(nullable = false)
  private LocalDateTime agregadoEn;

  private Integer temporadaActual;

  private Integer unidadActual;

  private Integer capituloActual;

  private Integer paginaActual;

  private Boolean esFavorito = false;

  public CatalogoPersonal() {
    this.temporadaActual = 1;
    this.unidadActual = 0;
    this.capituloActual = 0;
    this.paginaActual = 0;
  }

  @PrePersist
  protected void onCreate() {
    if (agregadoEn == null) {
      agregadoEn = LocalDateTime.now();
    }
    if (this.temporadaActual == null) {
      this.temporadaActual = 1;
    }
    if (this.unidadActual == null) {
      this.unidadActual = 0;
    }
    if (this.capituloActual == null) {
      this.capituloActual = 0;
    }
    if (this.paginaActual == null) {
      this.paginaActual = 0;
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public Elemento getElemento() {
    return elemento;
  }

  public void setElemento(Elemento elemento) {
    this.elemento = elemento;
  }

  public EstadoPersonal getEstadoPersonal() {
    return estadoPersonal;
  }

  public void setEstadoPersonal(EstadoPersonal estadoPersonal) {
    this.estadoPersonal = estadoPersonal;
  }

  public LocalDateTime getAgregadoEn() {
    return agregadoEn;
  }

  public void setAgregadoEn(LocalDateTime agregadoEn) {
    this.agregadoEn = agregadoEn;
  }

  public Integer getTemporadaActual() {
    return temporadaActual;
  }

  public void setTemporadaActual(Integer temporadaActual) {
    this.temporadaActual = temporadaActual;
  }

  public Integer getUnidadActual() {
    return unidadActual;
  }

  public void setUnidadActual(Integer unidadActual) {
    this.unidadActual = unidadActual;
  }

  public Integer getCapituloActual() {
    return capituloActual;
  }

  public void setCapituloActual(Integer capituloActual) {
    this.capituloActual = capituloActual;
  }

  public Integer getPaginaActual() {
    return paginaActual;
  }

  public void setPaginaActual(Integer paginaActual) {
    this.paginaActual = paginaActual;
  }

  public Boolean getEsFavorito() {
    return esFavorito;
  }

  public void setEsFavorito(Boolean esFavorito) {
    this.esFavorito = esFavorito;
  }
}
