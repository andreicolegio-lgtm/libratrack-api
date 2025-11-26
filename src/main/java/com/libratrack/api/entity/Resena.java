package com.libratrack.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa una opinión o valoración escrita por un usuario sobre un elemento específico.
 *
 * <p>Cada usuario puede escribir una única reseña por elemento, garantizado por la restricción
 * única en la base de datos (usuario_id + elemento_id).
 */
@Entity
@Table(
    name = "resenas",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {"usuario_id", "elemento_id"},
          name = "uk_resena_usuario_elemento")
    })
public class Resena {

  // =============================================================================================
  // IDENTIFICADOR
  // =============================================================================================

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // =============================================================================================
  // RELACIONES
  // =============================================================================================

  /** Usuario autor de la reseña. */
  @NotNull(message = "{validation.resena.usuario.required}")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  /** Elemento sobre el cual se opina. */
  @NotNull(message = "{validation.resena.elemento.required}")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "elemento_id", nullable = false)
  private Elemento elemento;

  // =============================================================================================
  // CONTENIDO
  // =============================================================================================

  /** Puntuación numérica del 1 al 5. */
  @Min(value = 1, message = "{validation.resena.valoracion.min}")
  @Max(value = 5, message = "{validation.resena.valoracion.max}")
  @Column(nullable = false)
  @NotNull(message = "{validation.resena.valoracion.required}")
  private Integer valoracion;

  /** Texto opcional de la opinión. */
  @Size(max = 2000, message = "{validation.resena.texto.size}")
  @Lob
  @Column(columnDefinition = "TEXT")
  private String textoResena;

  /** Fecha de publicación. No se puede modificar una vez creada. */
  @Column(nullable = false, updatable = false)
  private LocalDateTime fechaCreacion;

  // =============================================================================================
  // CONSTRUCTORES & CICLO DE VIDA
  // =============================================================================================

  public Resena() {}

  @PrePersist
  protected void onCrear() {
    this.fechaCreacion = LocalDateTime.now();
  }

  // =============================================================================================
  // GETTERS Y SETTERS
  // =============================================================================================

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

  // =============================================================================================
  // EQUALS, HASHCODE & TOSTRING
  // =============================================================================================

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Resena resena = (Resena) o;
    return id != null && Objects.equals(id, resena.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "Resena{"
        + "id="
        + id
        + ", valoracion="
        + valoracion
        + ", fechaCreacion="
        + fechaCreacion
        + '}';
  }
}
