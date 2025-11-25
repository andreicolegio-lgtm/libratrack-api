package com.libratrack.api.dto;

import com.libratrack.api.entity.Tipo;
import java.util.List;
import java.util.stream.Collectors;

public class TipoResponseDTO {

  private Long id;
  private String nombre;
  private List<GeneroResponseDTO> generosPermitidos;

  public TipoResponseDTO(Tipo tipo) {
    this.id = tipo.getId();
    this.nombre = tipo.getNombre();
    this.generosPermitidos = tipo.getGenerosPermitidos()
                                     .stream()
                                     .map(GeneroResponseDTO::new)
                                     .collect(Collectors.toList());
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public List<GeneroResponseDTO> getGenerosPermitidos() {
    return generosPermitidos;
  }
}
