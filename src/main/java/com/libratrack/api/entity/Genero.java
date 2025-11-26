package com.libratrack.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;

/**
 * Representa una categoría temática, estilo o clasificación (Género) asignable a un {@link
 * Elemento}.
 *
 * <p>Ejemplos: "Acción", "Fantasía", "RPG", "Terror", "No Ficción". Esta entidad es utilizada
 * transversalmente por varios tipos de contenido.
 */
@Entity
@Table(name = "generos")
public class Genero {

  // =============================================================================================
  // IDENTIFICADOR
  // =============================================================================================

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // =============================================================================================
  // DATOS DEL GÉNERO
  // =============================================================================================

  /**
   * Nombre único del género. Se define como único en la base de datos para evitar duplicidades (ej.
   * tener "Accion" y "Acción").
   */
  @NotBlank(message = "{validation.genero.nombre.required}")
  @Size(max = 50, message = "{validation.genero.nombre.size}")
  @Column(unique = true, nullable = false, length = 50)
  private String nombre;

  // =============================================================================================
  // CONSTRUCTORES
  // =============================================================================================

  public Genero() {}

  public Genero(String nombre) {
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

  // =============================================================================================
  // EQUALS, HASHCODE & TOSTRING
  // =============================================================================================

  /**
   * Implementación de igualdad basada en el ID. Fundamental para el correcto funcionamiento en
   * colecciones Set (ej. elemento.getGeneros()).
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Genero genero = (Genero) o;
    return id != null && Objects.equals(id, genero.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "Genero{" + "id=" + id + ", nombre='" + nombre + '\'' + '}';
  }
}
