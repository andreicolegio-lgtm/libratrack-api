package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

/**
 * DTO para la creación o transferencia básica de datos de un Elemento. Utilizado principalmente en
 * operaciones internas o APIs simplificadas.
 */
public class ElementoDTO {

  @NotBlank(message = "{validation.elemento.titulo.required}")
  private String titulo;

  @NotBlank(message = "{validation.elemento.descripcion.required}")
  private String descripcion;

  private String urlImagen;
  private LocalDate fechaLanzamiento;

  @NotNull(message = "{validation.elemento.tipo.required}")
  private Long tipoId;

  @NotNull(message = "{validation.elemento.creador.required}")
  private Long creadorId;

  @NotNull(message = "{validation.elemento.generos.required}")
  private Set<Long> generoIds;

  private String duracion;

  // Getters y Setters
  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getUrlImagen() {
    return urlImagen;
  }

  public void setUrlImagen(String urlImagen) {
    this.urlImagen = urlImagen;
  }

  public LocalDate getFechaLanzamiento() {
    return fechaLanzamiento;
  }

  public void setFechaLanzamiento(LocalDate fechaLanzamiento) {
    this.fechaLanzamiento = fechaLanzamiento;
  }

  public Long getTipoId() {
    return tipoId;
  }

  public void setTipoId(Long tipoId) {
    this.tipoId = tipoId;
  }

  public Long getCreadorId() {
    return creadorId;
  }

  public void setCreadorId(Long creadorId) {
    this.creadorId = creadorId;
  }

  public Set<Long> getGeneroIds() {
    return generoIds;
  }

  public void setGeneroIds(Set<Long> generoIds) {
    this.generoIds = generoIds;
  }

  public String getDuracion() {
    return duracion;
  }

  public void setDuracion(String duracion) {
    this.duracion = duracion;
  }
}
