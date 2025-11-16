package com.libratrack.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResenaDTO {

  @NotNull(message = "El ID del elemento no puede ser nulo")
  private Long elementoId;

  @NotNull(message = "La valoración no puede ser nula")
  @Min(value = 1, message = "La valoración mínima es 1")
  @Max(value = 5, message = "La valoración máxima es 5")
  private Integer valoracion;

  @Size(max = 2000, message = "La reseña no puede exceder los 2000 caracteres")
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
