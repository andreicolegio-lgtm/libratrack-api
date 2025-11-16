package com.libratrack.api.dto;

import com.libratrack.api.model.EstadoPersonal;
import jakarta.validation.constraints.Min;

public class CatalogoUpdateDTO {

  private EstadoPersonal estadoPersonal;

  @Min(value = 1, message = "La temporada mínima es 1")
  private Integer temporadaActual;

  @Min(value = 0, message = "La unidad mínima es 0 (antes de empezar)")
  private Integer unidadActual;

  @Min(value = 0, message = "El capítulo mínimo es 0")
  private Integer capituloActual;

  @Min(value = 0, message = "La página mínima es 0")
  private Integer paginaActual;

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
}
