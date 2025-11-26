package com.libratrack.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** DTO para crear una nueva reseña. */
public class ResenaDTO {

  @NotNull(message = "{validation.resena.elemento.required}")
  private Long elementoId;

  @NotNull(message = "{validation.resena.valoracion.required}")
  @Min(value = 1, message = "{validation.resena.valoracion.min}")
  @Max(value = 5, message = "{validation.resena.valoracion.max}")
  private Integer valoracion;

  @Size(max = 2000, message = "{validation.resena.texto.size}")
  private String textoResena;

  // Getters y Setters
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
