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

/**
 * DTO completo para mostrar los detalles de un Elemento. Incluye lógica defensiva para manejar
 * relaciones Lazy que podrían no estar inicializadas.
 */
public class ElementoResponseDTO {

  private Long id;
  private String titulo;
  private String descripcion;
  private String urlImagen;
  private LocalDate fechaLanzamiento;
  private String tipoNombre;
  private EstadoContenido estadoContenido;
  private String creadorUsername;
  private String autorNombre;
  private String autorEmail;
  private Set<String> generos;

  private EstadoPublicacion estadoPublicacion;
  private String episodiosPorTemporada;
  private Integer totalUnidades;
  private Integer totalCapitulosLibro;
  private Integer totalPaginasLibro;

  private Set<ElementoRelacionDTO> precuelas;
  private Set<ElementoRelacionDTO> secuelas;
  private String duracion;

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
    this.duracion = elemento.getDuracion();

    // Inicialización segura de Tipo
    if (Hibernate.isInitialized(elemento.getTipo()) && elemento.getTipo() != null) {
      this.tipoNombre = elemento.getTipo().getNombre();
    } else {
      this.tipoNombre = null;
    }

    // Inicialización segura de Creador
    if (Hibernate.isInitialized(elemento.getCreador()) && elemento.getCreador() != null) {
      this.creadorUsername = elemento.getCreador().getUsername();
      this.autorNombre = elemento.getCreador().getUsername();
      this.autorEmail = elemento.getCreador().getEmail();
    } else {
      this.creadorUsername = null;
      this.autorNombre = null;
      this.autorEmail = null;
    }

    // Inicialización segura de Géneros
    if (Hibernate.isInitialized(elemento.getGeneros()) && elemento.getGeneros() != null) {
      this.generos =
          elemento.getGeneros().stream().map(Genero::getNombre).collect(Collectors.toSet());
    } else {
      this.generos = Collections.emptySet();
    }

    // Inicialización segura de Precuelas
    if (Hibernate.isInitialized(elemento.getPrecuelas()) && elemento.getPrecuelas() != null) {
      this.precuelas =
          elemento.getPrecuelas().stream()
              .map(ElementoRelacionDTO::new)
              .collect(Collectors.toSet());
    } else {
      this.precuelas = Collections.emptySet();
    }

    // Inicialización segura de Secuelas
    if (Hibernate.isInitialized(elemento.getSecuelas()) && elemento.getSecuelas() != null) {
      this.secuelas =
          elemento.getSecuelas().stream().map(ElementoRelacionDTO::new).collect(Collectors.toSet());
    } else {
      this.secuelas = Collections.emptySet();
    }
  }

  // Getters y Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getUrlImagen() {
    return urlImagen;
  }

  public void setUrlImagen(String urlImagen) {
    this.urlImagen = urlImagen;
  }

  public LocalDate getFechaLanzamiento() {
    return fechaLanzamiento;
  }

  public void setFechaLanzamiento(LocalDate fechaLanzamiento) {
    this.fechaLanzamiento = fechaLanzamiento;
  }

  public String getTipoNombre() {
    return tipoNombre;
  }

  public void setTipoNombre(String tipoNombre) {
    this.tipoNombre = tipoNombre;
  }

  public EstadoContenido getEstadoContenido() {
    return estadoContenido;
  }

  public void setEstadoContenido(EstadoContenido estadoContenido) {
    this.estadoContenido = estadoContenido;
  }

  public String getCreadorUsername() {
    return creadorUsername;
  }

  public void setCreadorUsername(String creadorUsername) {
    this.creadorUsername = creadorUsername;
  }

  public String getAutorNombre() {
    return autorNombre;
  }

  public void setAutorNombre(String autorNombre) {
    this.autorNombre = autorNombre;
  }

  public String getAutorEmail() {
    return autorEmail;
  }

  public void setAutorEmail(String autorEmail) {
    this.autorEmail = autorEmail;
  }

  public Set<String> getGeneros() {
    return generos;
  }

  public void setGeneros(Set<String> generos) {
    this.generos = generos;
  }

  public EstadoPublicacion getEstadoPublicacion() {
    return estadoPublicacion;
  }

  public void setEstadoPublicacion(EstadoPublicacion estadoPublicacion) {
    this.estadoPublicacion = estadoPublicacion;
  }

  public String getEpisodiosPorTemporada() {
    return episodiosPorTemporada;
  }

  public void setEpisodiosPorTemporada(String episodiosPorTemporada) {
    this.episodiosPorTemporada = episodiosPorTemporada;
  }

  public Integer getTotalUnidades() {
    return totalUnidades;
  }

  public void setTotalUnidades(Integer totalUnidades) {
    this.totalUnidades = totalUnidades;
  }

  public Integer getTotalCapitulosLibro() {
    return totalCapitulosLibro;
  }

  public void setTotalCapitulosLibro(Integer totalCapitulosLibro) {
    this.totalCapitulosLibro = totalCapitulosLibro;
  }

  public Integer getTotalPaginasLibro() {
    return totalPaginasLibro;
  }

  public void setTotalPaginasLibro(Integer totalPaginasLibro) {
    this.totalPaginasLibro = totalPaginasLibro;
  }

  public Set<ElementoRelacionDTO> getPrecuelas() {
    return precuelas;
  }

  public void setPrecuelas(Set<ElementoRelacionDTO> precuelas) {
    this.precuelas = precuelas;
  }

  public Set<ElementoRelacionDTO> getSecuelas() {
    return secuelas;
  }

  public void setSecuelas(Set<ElementoRelacionDTO> secuelas) {
    this.secuelas = secuelas;
  }

  public String getDuracion() {
    return duracion;
  }

  public void setDuracion(String duracion) {
    this.duracion = duracion;
  }
}
