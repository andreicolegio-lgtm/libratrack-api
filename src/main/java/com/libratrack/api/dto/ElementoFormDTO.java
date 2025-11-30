package com.libratrack.api.dto;

import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPublicacion;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO utilizado en formularios administrativos para Crear o Editar un Elemento completo. Incluye
 * validaciones de formato y campos para relaciones por nombre (Tipo, Géneros) para facilitar la
 * entrada de datos desde el frontend.
 */
public class ElementoFormDTO {

  @NotBlank(message = "{validation.elemento.titulo.required}")
  @Size(max = 255, message = "{validation.elemento.titulo.size}")
  private String titulo;

  @Size(max = 5000, message = "{validation.elemento.descripcion.size}")
  private String descripcion;

  @NotBlank(message = "{validation.elemento.tipo.required}")
  private String tipoNombre;

  @NotBlank(message = "{validation.elemento.generos.required}")
  private String generosNombres;

  @Size(max = 255)
  private String urlImagen;

  @Size(max = 255)
  private String episodiosPorTemporada;

  @Min(value = 1, message = "{validation.elemento.unidades.min}")
  private Integer totalUnidades;

  @Min(value = 1, message = "{validation.elemento.capitulos.min}")
  private Integer totalCapitulosLibro;

  @Min(value = 1, message = "{validation.elemento.paginas.min}")
  private Integer totalPaginasLibro;

  private List<Long> secuelaIds;

  @Size(max = 255)
  private String duracion;

  private EstadoPublicacion estadoPublicacion;

  private EstadoContenido estadoContenido;

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

  public EstadoPublicacion getEstadoPublicacion() {
    return estadoPublicacion;
  }

  public void setEstadoPublicacion(EstadoPublicacion estadoPublicacion) {
    this.estadoPublicacion = estadoPublicacion;
  }

  public EstadoContenido getEstadoContenido() {
    return estadoContenido;
  }

  public void setEstadoContenido(EstadoContenido estadoContenido) {
    this.estadoContenido = estadoContenido;
  }
}
