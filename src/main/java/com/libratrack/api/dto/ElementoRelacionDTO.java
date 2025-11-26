package com.libratrack.api.dto;

import com.libratrack.api.entity.Elemento;

/**
 * DTO ligero para representar referencias a elementos (ej. en listas de secuelas, resultados de
 * búsqueda rápida). Contiene solo la información mínima necesaria para identificar visualmente el
 * contenido.
 */
public class ElementoRelacionDTO {

  private Long id;
  private String titulo;
  private String urlImagen;

  public ElementoRelacionDTO(Elemento elemento) {
    this.id = elemento.getId();
    this.titulo = elemento.getTitulo();
    this.urlImagen = elemento.getUrlImagen();
  }

  // Getters
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
