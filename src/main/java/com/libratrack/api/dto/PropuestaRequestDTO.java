package com.libratrack.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO para recibir una nueva sugerencia de contenido por parte de un usuario. */
public class PropuestaRequestDTO {

  @NotBlank(message = "{validation.propuesta.titulo.required}")
  @Size(max = 255, message = "{validation.propuesta.titulo.size}")
  private String tituloSugerido;

  @NotBlank(message = "{validation.propuesta.descripcion.required}")
  @Size(max = 5000, message = "{validation.propuesta.descripcion.size}")
  private String descripcionSugerida;

  @NotBlank(message = "{validation.propuesta.tipo.required}")
  private String tipoSugerido;

  @NotBlank(message = "{validation.propuesta.generos.required}")
  private String generosSugeridos;

  private String imagenPortadaUrl;

  @Size(max = 255, message = "{validation.propuesta.episodios.size}")
  private String episodiosPorTemporada;

  @Min(value = 1, message = "{validation.propuesta.unidades.min}")
  private Integer totalUnidades;

  @Min(value = 1, message = "{validation.propuesta.capitulos.min}")
  private Integer totalCapitulosLibro;

  @Min(value = 1, message = "{validation.propuesta.paginas.min}")
  private Integer totalPaginasLibro;

  private String duracion;

  // Getters y Setters
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

  public String getDuracion() {
    return duracion;
  }

  public void setDuracion(String duracion) {
    this.duracion = duracion;
  }
}
