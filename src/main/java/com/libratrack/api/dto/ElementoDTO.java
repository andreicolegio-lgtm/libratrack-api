package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

public class ElementoDTO {

  @NotBlank(message = "VALIDATION_TITLE_REQUIRED")
  private String titulo;

  @NotBlank(message = "VALIDATION_DESC_REQUIRED")
  private String descripcion;

  private String urlImagen;
  private LocalDate fechaLanzamiento;

  @NotNull(message = "VALIDATION_TYPE_ID_REQUIRED")
  private Long tipoId;

  @NotNull(message = "VALIDATION_CREATOR_ID_REQUIRED")
  private Long creadorId;

  @NotNull(message = "VALIDATION_GENRE_IDS_REQUIRED")
  private Set<Long> generoIds;

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
}
