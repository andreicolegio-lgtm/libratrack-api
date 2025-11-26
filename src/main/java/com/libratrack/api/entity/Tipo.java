package com.libratrack.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Representa la clasificación principal de un contenido (Medio).
 *
 * <p>Ejemplos: "Anime", "Manga", "Videojuego", "Película". Cada tipo define qué géneros son válidos
 * para él a través de {@link #generosPermitidos}.
 */
@Entity
@Table(name = "tipos")
public class Tipo {

  // =============================================================================================
  // IDENTIFICADOR
  // =============================================================================================

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // =============================================================================================
  // INFORMACIÓN DEL TIPO
  // =============================================================================================

  @NotBlank(message = "{validation.tipo.nombre.required}")
  @Size(max = 50, message = "{validation.tipo.nombre.size}")
  @Column(unique = true, nullable = false, length = 50)
  private String nombre;

  /**
   * Colección de géneros que lógicamente aplican a este tipo de medio. Ayuda a filtrar en el
   * frontend (ej. no mostrar "Plataformas" si el tipo es "Libro").
   */
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "tipo_genero",
      joinColumns = @JoinColumn(name = "tipo_id"),
      inverseJoinColumns = @JoinColumn(name = "genero_id"))
  private Set<Genero> generosPermitidos = new HashSet<>();

  // =============================================================================================
  // CONSTRUCTORES
  // =============================================================================================

  public Tipo() {}

  public Tipo(String nombre) {
    this.nombre = nombre;
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

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Set<Genero> getGenerosPermitidos() {
    return generosPermitidos;
  }

  public void setGenerosPermitidos(Set<Genero> generosPermitidos) {
    this.generosPermitidos = generosPermitidos;
  }

  // =============================================================================================
  // EQUALS, HASHCODE & TOSTRING
  // =============================================================================================

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Tipo tipo = (Tipo) o;
    return id != null && Objects.equals(id, tipo.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "Tipo{" + "id=" + id + ", nombre='" + nombre + '\'' + '}';
  }
}
