package com.libratrack.api.dto;

import com.libratrack.api.entity.CatalogoPersonal;
import com.libratrack.api.model.EstadoPersonal;
import com.libratrack.api.model.EstadoPublicacion;
import java.time.LocalDateTime;

public class CatalogoPersonalResponseDTO {

  private Long id;
  private EstadoPersonal estadoPersonal;
  private LocalDateTime agregadoEn;

  private Integer temporadaActual;
  private Integer unidadActual;
  private Integer capituloActual;
  private Integer paginaActual;

  private Long elementoId;
  private String elementoTitulo;
  private String elementoTipoNombre;
  private String elementoUrlImagen;

  private EstadoPublicacion elementoEstadoPublicacion;
  private String elementoEpisodiosPorTemporada;
  private Integer elementoTotalUnidades;
  private Integer elementoTotalCapitulosLibro;
  private Integer elementoTotalPaginasLibro;

  private Long usuarioId;

  public CatalogoPersonalResponseDTO(CatalogoPersonal entrada) {
    this.id = entrada.getId();
    this.estadoPersonal = entrada.getEstadoPersonal();
    this.agregadoEn = entrada.getAgregadoEn();

    this.temporadaActual = entrada.getTemporadaActual();
    this.unidadActual = entrada.getUnidadActual();
    this.capituloActual = entrada.getCapituloActual();
    this.paginaActual = entrada.getPaginaActual();

    this.elementoId = entrada.getElemento().getId();
    this.elementoTitulo = entrada.getElemento().getTitulo();
    this.elementoTipoNombre = entrada.getElemento().getTipo().getNombre();
    this.elementoUrlImagen = entrada.getElemento().getUrlImagen();

    this.elementoEstadoPublicacion = entrada.getElemento().getEstadoPublicacion();
    this.elementoEpisodiosPorTemporada = entrada.getElemento().getEpisodiosPorTemporada();
    this.elementoTotalUnidades = entrada.getElemento().getTotalUnidades();
    this.elementoTotalCapitulosLibro = entrada.getElemento().getTotalCapitulosLibro();
    this.elementoTotalPaginasLibro = entrada.getElemento().getTotalPaginasLibro();

    this.usuarioId = entrada.getUsuario().getId();
  }

  public Long getId() {
    return id;
  }

  public EstadoPersonal getEstadoPersonal() {
    return estadoPersonal;
  }

  public LocalDateTime getAgregadoEn() {
    return agregadoEn;
  }

  public Integer getTemporadaActual() {
    return temporadaActual;
  }

  public Integer getUnidadActual() {
    return unidadActual;
  }

  public Integer getCapituloActual() {
    return capituloActual;
  }

  public Integer getPaginaActual() {
    return paginaActual;
  }

  public Long getElementoId() {
    return elementoId;
  }

  public String getElementoTitulo() {
    return elementoTitulo;
  }

  public String getElementoTipoNombre() {
    return elementoTipoNombre;
  }

  public String getElementoUrlImagen() {
    return elementoUrlImagen;
  }

  public EstadoPublicacion getElementoEstadoPublicacion() {
    return elementoEstadoPublicacion;
  }

  public String getElementoEpisodiosPorTemporada() {
    return elementoEpisodiosPorTemporada;
  }

  public Integer getElementoTotalUnidades() {
    return elementoTotalUnidades;
  }

  public Integer getElementoTotalCapitulosLibro() {
    return elementoTotalCapitulosLibro;
  }

  public Integer getElementoTotalPaginasLibro() {
    return elementoTotalPaginasLibro;
  }

  public Long getUsuarioId() {
    return usuarioId;
  }
}
