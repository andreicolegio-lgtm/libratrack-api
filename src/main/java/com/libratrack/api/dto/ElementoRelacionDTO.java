package com.libratrack.api.dto;

import com.libratrack.api.entity.Elemento;

public class ElementoRelacionDTO {

  private Long id;
  private String titulo;
  private String urlImagen;

  public ElementoRelacionDTO(Elemento elemento) {
    this.id = elemento.getId();
    this.titulo = elemento.getTitulo();
    this.urlImagen = elemento.getUrlImagen();
  }

  public Long getId() {
    return id;
  }

  public String getTitulo() {
    return titulo;
  }

  public String getUrlImagen() {
    return urlImagen;
  }
}
