package com.libratrack.api.dto;

import com.libratrack.api.entity.Genero;

/** DTO ligero para enviar información de género (ID y nombre) al cliente. */
public class GeneroResponseDTO {

  private Long id;
  private String nombre;

  public GeneroResponseDTO(Genero genero) {
    this.id = genero.getId();
    this.nombre = genero.getNombre();
  }

  // Getters y Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
}
