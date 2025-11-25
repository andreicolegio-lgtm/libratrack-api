package com.libratrack.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public class ElementoFormDTO {

  @NotBlank(message = "VALIDATION_TITLE_REQUIRED")
  @Size(max = 255)
  private String titulo;

  @NotBlank(message = "VALIDATION_DESC_REQUIRED")
  @Size(max = 5000)
  private String descripcion;

  @NotBlank(message = "VALIDATION_TYPE_REQUIRED")
  private String tipoNombre;

  @NotBlank(message = "VALIDATION_GENRES_REQUIRED")
  private String generosNombres;

  @Size(max = 255)
  private String urlImagen;

  @Size(max = 255)
  private String episodiosPorTemporada;

  @Min(value = 1)
  private Integer totalUnidades;

  @Min(value = 1)
  private Integer totalCapitulosLibro;

  @Min(value = 1)
  private Integer totalPaginasLibro;

  private List<Long> secuelaIds;

  @Size(max = 255)
  private String duracion;

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

  public String getTipoNombre() {
    return tipoNombre;
  }

  public void setTipoNombre(String tipoNombre) {
    this.tipoNombre = tipoNombre;
  }

  public String getGenerosNombres() {
    return generosNombres;
  }

  public void setGenerosNombres(String generosNombres) {
    this.generosNombres = generosNombres;
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

  public List<Long> getSecuelaIds() {
    return secuelaIds;
  }

  public void setSecuelaIds(List<Long> secuelaIds) {
    this.secuelaIds = secuelaIds;
  }

  public String getDuracion() {
    return duracion;
  }

  public void setDuracion(String duracion) {
    this.duracion = duracion;
  }
}
