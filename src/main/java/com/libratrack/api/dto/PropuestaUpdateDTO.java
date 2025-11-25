package com.libratrack.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PropuestaUpdateDTO {

  @NotBlank(message = "VALIDATION_TITLE_REQUIRED")
  @Size(max = 255, message = "VALIDATION_TITLE_MAX_255")
  private String tituloSugerido;

  @NotBlank(message = "VALIDATION_DESC_REQUIRED")
  @Size(max = 5000, message = "VALIDATION_DESC_MAX_5000")
  private String descripcionSugerida;

  @NotBlank(message = "VALIDATION_TYPE_REQUIRED")
  private String tipoSugerido;

  @NotBlank(message = "VALIDATION_GENRES_REQUIRED")
  private String generosSugeridos;

  @Size(max = 255)
  private String urlImagen;

  @Size(max = 255, message = "VALIDATION_EPISODES_STRING_MAX_255")
  private String episodiosPorTemporada;

  @Min(value = 1, message = "VALIDATION_UNITS_MIN_1")
  private Integer totalUnidades;

  @Min(value = 1, message = "VALIDATION_CHAPTERS_MIN_1")
  private Integer totalCapitulosLibro;

  @Min(value = 1, message = "VALIDATION_PAGES_MIN_1")
  private Integer totalPaginasLibro;

  private String duracion;

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

  public String getDuracion() {
    return duracion;
  }

  public void setDuracion(String duracion) {
    this.duracion = duracion;
  }
}
