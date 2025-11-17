package com.libratrack.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResenaDTO {

  @NotNull(message = "VALIDATION_ELEMENT_ID_REQUIRED")
  private Long elementoId;

  @NotNull(message = "VALIDATION_RATING_REQUIRED")
  @Min(value = 1, message = "VALIDATION_RATING_MIN_1")
  @Max(value = 5, message = "VALIDATION_RATING_MAX_5")
  private Integer valoracion;

  @Size(max = 2000, message = "VALIDATION_REVIEW_MAX_2000")
  private String textoResena;

  public Long getElementoId() {
    return elementoId;
  }

  public void setElementoId(Long elementoId) {
    this.elementoId = elementoId;
  }

  public Integer getValoracion() {
    return valoracion;
  }

  public void setValoracion(Integer valoracion) {
    this.valoracion = valoracion;
  }

  public String getTextoResena() {
    return textoResena;
  }

  public void setTextoResena(String textoResena) {
    this.textoResena = textoResena;
  }
}
