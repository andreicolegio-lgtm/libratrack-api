package com.libratrack.api.dto;

import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Genero;
import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPublicacion;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.Hibernate;

public class ElementoResponseDTO {

  private Long id;
  private String titulo;
  private String descripcion;
  private String urlImagen;
  private LocalDate fechaLanzamiento;
  private String tipoNombre;
  private EstadoContenido estadoContenido;
  private String creadorUsername;
  private Set<String> generos;

  private EstadoPublicacion estadoPublicacion;
  private String episodiosPorTemporada;
  private Integer totalUnidades;
  private Integer totalCapitulosLibro;
  private Integer totalPaginasLibro;

  private Set<ElementoRelacionDTO> precuelas;
  private Set<ElementoRelacionDTO> secuelas;

  public ElementoResponseDTO(Elemento elemento) {

    this.id = elemento.getId();
    this.titulo = elemento.getTitulo();
    this.descripcion = elemento.getDescripcion();
    this.urlImagen = elemento.getUrlImagen();
    this.fechaLanzamiento = elemento.getFechaLanzamiento();
    this.estadoContenido = elemento.getEstadoContenido();
    this.estadoPublicacion = elemento.getEstadoPublicacion();
    this.episodiosPorTemporada = elemento.getEpisodiosPorTemporada();
    this.totalUnidades = elemento.getTotalUnidades();
    this.totalCapitulosLibro = elemento.getTotalCapitulosLibro();
    this.totalPaginasLibro = elemento.getTotalPaginasLibro();

    if (Hibernate.isInitialized(elemento.getTipo()) && elemento.getTipo() != null) {
      this.tipoNombre = elemento.getTipo().getNombre();
    } else {
      this.tipoNombre = null;
    }

    if (Hibernate.isInitialized(elemento.getCreador()) && elemento.getCreador() != null) {
      this.creadorUsername = elemento.getCreador().getUsername();
    } else {
      this.creadorUsername = "OFICIAL";
    }

    if (Hibernate.isInitialized(elemento.getGeneros()) && elemento.getGeneros() != null) {
      this.generos =
          elemento.getGeneros().stream().map(Genero::getNombre).collect(Collectors.toSet());
    } else {
      this.generos = Collections.emptySet();
    }

    if (Hibernate.isInitialized(elemento.getPrecuelas()) && elemento.getPrecuelas() != null) {
      this.precuelas =
          elemento.getPrecuelas().stream()
              .map(ElementoRelacionDTO::new)
              .collect(Collectors.toSet());
    } else {
      this.precuelas = Collections.emptySet();
    }

    if (Hibernate.isInitialized(elemento.getSecuelas()) && elemento.getSecuelas() != null) {
      this.secuelas =
          elemento.getSecuelas().stream().map(ElementoRelacionDTO::new).collect(Collectors.toSet());
    } else {
      this.secuelas = Collections.emptySet();
    }
  }

  public Long getId() {
    return id;
  }

  public String getTitulo() {
    return titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public String getUrlImagen() {
    return urlImagen;
  }

  public LocalDate getFechaLanzamiento() {
    return fechaLanzamiento;
  }

  public String getTipoNombre() {
    return tipoNombre;
  }

  public EstadoContenido getEstadoContenido() {
    return estadoContenido;
  }

  public String getCreadorUsername() {
    return creadorUsername;
  }

  public Set<String> getGeneros() {
    return generos;
  }

  public EstadoPublicacion getEstadoPublicacion() {
    return estadoPublicacion;
  }

  public String getEpisodiosPorTemporada() {
    return episodiosPorTemporada;
  }

  public Integer getTotalUnidades() {
    return totalUnidades;
  }

  public Integer getTotalCapitulosLibro() {
    return totalCapitulosLibro;
  }

  public Integer getTotalPaginasLibro() {
    return totalPaginasLibro;
  }

  public Set<ElementoRelacionDTO> getPrecuelas() {
    return precuelas;
  }

  public Set<ElementoRelacionDTO> getSecuelas() {
    return secuelas;
  }
}
