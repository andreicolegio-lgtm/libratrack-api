package com.libratrack.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "resenas",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"usuario_id", "elemento_id"})})
public class Resena {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id", nullable = false)
  @NotNull
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "elemento_id", nullable = false)
  @NotNull
  private Elemento elemento;

  @Min(value = 1, message = "VALIDATION_RATING_MIN_1")
  @Max(value = 5, message = "VALIDATION_RATING_MAX_5")
  @Column(nullable = false)
  @NotNull(message = "VALIDATION_RATING_REQUIRED")
  private Integer valoracion;

  @Size(max = 2000, message = "VALIDATION_REVIEW_MAX_2000")
  @Lob
  private String textoResena;

  @Column(nullable = false, updatable = false)
  private LocalDateTime fechaCreacion;

  @PrePersist
  protected void onCrear() {
    this.fechaCreacion = LocalDateTime.now();
  }

  public Resena() {}

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

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }
}