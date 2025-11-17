package com.libratrack.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PropuestaRequestDTO {

  @NotBlank(message = "VALIDATION_TITLE_REQUIRED")
  @Size(max = 255)
  private String tituloSugerido;

  @NotBlank(message = "VALIDATION_DESC_REQUIRED")
  @Size(max = 5000)
  private String descripcionSugerida;

  @NotBlank(message = "VALIDATION_TYPE_REQUIRED")
  private String tipoSugerido;

  @NotBlank(message = "VALIDATION_GENRES_REQUIRED")
  private String generosSugeridos;

  private String imagenPortadaUrl;

  @Size(max = 255, message = "La cadena de episodios por temporada es muy larga")
  private String episodiosPorTemporada;

  @Min(value = 1)
  private Integer totalUnidades;

  @Min(value = 1)
  private Integer totalCapitulosLibro;

  @Min(value = 1)
  private Integer totalPaginasLibro;

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

  public String getImagenPortadaUrl() {
    return imagenPortadaUrl;
  }

  public void setImagenPortadaUrl(String imagenPortadaUrl) {
    this.imagenPortadaUrl = imagenPortadaUrl;
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