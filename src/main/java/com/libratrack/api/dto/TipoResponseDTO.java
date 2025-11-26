package com.libratrack.api.dto;

import com.libratrack.api.entity.Tipo;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO para exponer los tipos de contenido (categorías) y sus géneros asociados. Ideal para llenar
 * menús desplegables dinámicos en el frontend.
 */
public class TipoResponseDTO {

  private Long id;
  private String nombre;
  private List<GeneroResponseDTO> generosPermitidos;

  public TipoResponseDTO(Tipo tipo) {
    this.id = tipo.getId();
    this.nombre = tipo.getNombre();
    if (tipo.getGenerosPermitidos() != null) {
      this.generosPermitidos =
          tipo.getGenerosPermitidos().stream()
              .map(GeneroResponseDTO::new)
              .collect(Collectors.toList());
    } else {
      this.generosPermitidos = List.of();
    }
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

  public List<GeneroResponseDTO> getGenerosPermitidos() {
    return generosPermitidos;
  }

  public void setGenerosPermitidos(List<GeneroResponseDTO> generosPermitidos) {
    this.generosPermitidos = generosPermitidos;
  }
}
