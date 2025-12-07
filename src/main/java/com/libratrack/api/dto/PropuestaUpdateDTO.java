package com.libratrack.api.dto;

import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPublicacion;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO utilizado por moderadores para editar y corregir una propuesta antes de su aprobación final.
 */
public class PropuestaUpdateDTO {

  @NotBlank(message = "{validation.propuesta.titulo.required}")
  @Size(max = 255, message = "{validation.propuesta.titulo.size}")
  private String tituloSugerido;

  @Size(max = 5000, message = "{validation.propuesta.descripcion.size}")
  private String descripcionSugerida; // Descripción ahora es opcional

  @NotBlank(message = "{validation.propuesta.tipo.required}")
  private String tipoSugerido;

  @NotBlank(message = "{validation.propuesta.generos.required}")
  private String generosSugeridos;

  @Size(max = 255)
  private String urlImagen;

  @Size(max = 255, message = "{validation.propuesta.episodios.size}")
  private String episodiosPorTemporada;

  @Min(value = 1, message = "{validation.propuesta.unidades.min}")
  private Integer totalUnidades;

  @Min(value = 1, message = "{validation.propuesta.capitulos.min}")
  private Integer totalCapitulosLibro;

  @Min(value = 1, message = "{validation.propuesta.paginas.min}")
  private Integer totalPaginasLibro;

  private String duracion;

  @Size(max = 500, message = "{validation.propuesta.comentarios.size}")
  private String comentariosRevision;

  private EstadoContenido estadoContenido; // OFICIAL o COMUNITARIO
  private EstadoPublicacion estadoPublicacion; // RELEASING, etc.

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

  public String getComentariosRevision() {
    return comentariosRevision;
  }

  public void setComentariosRevision(String comentariosRevision) {
    this.comentariosRevision = comentariosRevision;
  }

  public EstadoContenido getEstadoContenido() {
    return estadoContenido;
  }

  public void setEstadoContenido(EstadoContenido estadoContenido) {
    this.estadoContenido = estadoContenido;
  }

  public EstadoPublicacion getEstadoPublicacion() {
    return estadoPublicacion;
  }

  public void setEstadoPublicacion(EstadoPublicacion estadoPublicacion) {
    this.estadoPublicacion = estadoPublicacion;
  }
}
