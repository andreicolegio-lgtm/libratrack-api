package com.libratrack.api.dto;

import com.libratrack.api.entity.Genero;

public class GeneroResponseDTO {

  private Long id;
  private String nombre;

  public GeneroResponseDTO(Genero genero) {
    this.id = genero.getId();
    this.nombre = genero.getNombre();
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }
}
