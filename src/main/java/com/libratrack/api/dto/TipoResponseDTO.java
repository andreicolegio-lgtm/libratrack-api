package com.libratrack.api.dto;

import com.libratrack.api.entity.Tipo;

public class TipoResponseDTO {

  private Long id;
  private String nombre;

  public TipoResponseDTO(Tipo tipo) {
    this.id = tipo.getId();
    this.nombre = tipo.getNombre();
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }
}
