package com.libratrack.api.dto;

import com.libratrack.api.model.EstadoPersonal;
import jakarta.validation.constraints.Min;

/**
 * DTO para actualizar el estado o progreso de un elemento en el catálogo personal. Todos los campos
 * son opcionales; solo se actualizan los enviados (PATCH style).
 */
public class CatalogoUpdateDTO {

  private EstadoPersonal estadoPersonal;

  @Min(value = 1, message = "{validation.catalogo.temporada.min}")
  private Integer temporadaActual;

  @Min(value = 0, message = "{validation.catalogo.unidad.min}")
  private Integer unidadActual;

  @Min(value = 0, message = "{validation.catalogo.capitulo.min}")
  private Integer capituloActual;

  @Min(value = 0, message = "{validation.catalogo.pagina.min}")
  private Integer paginaActual;

  private String notas;

  // Getters y Setters
  public EstadoPersonal getEstadoPersonal() {
    return estadoPersonal;
  }

  public void setEstadoPersonal(EstadoPersonal estadoPersonal) {
    this.estadoPersonal = estadoPersonal;
  }

  public Integer getTemporadaActual() {
    return temporadaActual;
  }

  public void setTemporadaActual(Integer temporadaActual) {
    this.temporadaActual = temporadaActual;
  }

  public Integer getUnidadActual() {
    return unidadActual;
  }

  public void setUnidadActual(Integer unidadActual) {
    this.unidadActual = unidadActual;
  }

  public Integer getCapituloActual() {
    return capituloActual;
  }

  public void setCapituloActual(Integer capituloActual) {
    this.capituloActual = capituloActual;
  }

  public Integer getPaginaActual() {
    return paginaActual;
  }

  public void setPaginaActual(Integer paginaActual) {
    this.paginaActual = paginaActual;
  }

  public String getNotas() {
    return notas;
  }

  public void setNotas(String notas) {
    this.notas = notas;
  }
}
