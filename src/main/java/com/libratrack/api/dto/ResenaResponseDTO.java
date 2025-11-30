package com.libratrack.api.dto;

import com.libratrack.api.entity.Resena;
import java.time.LocalDateTime;

/** DTO para mostrar una reseña en el listado de comentarios de un elemento. */
public class ResenaResponseDTO {

  private Long id;
  private Integer valoracion;
  private String textoResena;
  private LocalDateTime fechaCreacion;

  private Long elementoId;
  private String usernameAutor;
  private String autorFotoPerfilUrl;
  private Long usuarioId; // Nuevo campo agregado

  public ResenaResponseDTO(Resena resena) {
    this.id = resena.getId();
    this.valoracion = resena.getValoracion();
    this.textoResena = resena.getTextoResena();
    this.fechaCreacion = resena.getFechaCreacion();

    if (resena.getElemento() != null) {
      this.elementoId = resena.getElemento().getId();
    }

    if (resena.getUsuario() != null) {
      this.usernameAutor = resena.getUsuario().getUsername();
      this.autorFotoPerfilUrl = resena.getUsuario().getFotoPerfilUrl();
      this.usuarioId = resena.getUsuario().getId(); // Asignar usuarioId
    } else {
      this.usernameAutor = "Usuario Eliminado";
    }
  }

  // Getters
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

  public Long getUsuarioId() {
    return usuarioId;
  }
}
