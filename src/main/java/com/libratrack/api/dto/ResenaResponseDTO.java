package com.libratrack.api.dto;

import com.libratrack.api.entity.Resena;
import java.time.LocalDateTime;

public class ResenaResponseDTO {

  private Long id;
  private Integer valoracion;
  private String textoResena;
  private LocalDateTime fechaCreacion;

  private Long elementoId;
  private String usernameAutor;
  private String autorFotoPerfilUrl;

  public ResenaResponseDTO(Resena resena) {
    this.id = resena.getId();
    this.valoracion = resena.getValoracion();
    this.textoResena = resena.getTextoResena();
    this.fechaCreacion = resena.getFechaCreacion();

    this.elementoId = resena.getElemento().getId();
    this.usernameAutor = resena.getUsuario().getUsername();

    this.autorFotoPerfilUrl = resena.getUsuario().getFotoPerfilUrl();
  }

  public Long getId() {
    return id;
  }

  public Integer getValoracion() {
    return valoracion;
  }

  public String getTextoResena() {
    return textoResena;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public Long getElementoId() {
    return elementoId;
  }

  public String getUsernameAutor() {
    return usernameAutor;
  }

  public String getAutorFotoPerfilUrl() {
    return autorFotoPerfilUrl;
  }
}
