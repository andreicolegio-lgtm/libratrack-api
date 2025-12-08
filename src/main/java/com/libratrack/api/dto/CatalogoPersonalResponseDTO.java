package com.libratrack.api.dto;

import com.libratrack.api.entity.CatalogoPersonal;
import com.libratrack.api.entity.Genero;
import com.libratrack.api.model.EstadoPersonal;
import com.libratrack.api.model.EstadoPublicacion;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * DTO para enviar los datos de una entrada del catálogo personal al cliente. Incluye información
 * combinada de la entrada personal y detalles resumen del elemento asociado.
 */
public class CatalogoPersonalResponseDTO {

  private Long id;
  private EstadoPersonal estadoPersonal;
  private LocalDateTime agregadoEn;
  private Boolean esFavorito;

  private Integer temporadaActual;
  private Integer unidadActual;
  private Integer capituloActual;
  private Integer paginaActual;

  // Datos del Elemento asociado (aplanados para facilitar el consumo en frontend)
  private Long elementoId;
  private String elementoTitulo;
  private String elementoTipoNombre;
  private String elementoUrlImagen;
  private EstadoPublicacion elementoEstadoPublicacion;

  // Detalles técnicos del elemento para calcular progreso (ej. 5/12 caps)
  private String elementoEpisodiosPorTemporada;
  private Integer elementoTotalUnidades;
  private Integer elementoTotalCapitulosLibro;
  private Integer elementoTotalPaginasLibro;

  private Long usuarioId;

  private String notas;
  private String elementoDuracion;
  private String elementoGeneros;

  public CatalogoPersonalResponseDTO(CatalogoPersonal entrada) {
    this.id = entrada.getId();
    this.estadoPersonal = entrada.getEstadoPersonal();
    this.agregadoEn = entrada.getAgregadoEn();
    this.esFavorito = entrada.getEsFavorito();

    this.temporadaActual = entrada.getTemporadaActual();
    this.unidadActual = entrada.getUnidadActual();
    this.capituloActual = entrada.getCapituloActual();
    this.paginaActual = entrada.getPaginaActual();

    this.notas = entrada.getNotas();

    if (entrada.getElemento() != null) {
      this.elementoId = entrada.getElemento().getId();
      this.elementoTitulo = entrada.getElemento().getTitulo();
      this.elementoUrlImagen = entrada.getElemento().getUrlImagen();
      this.elementoEstadoPublicacion = entrada.getElemento().getEstadoPublicacion();

      this.elementoEpisodiosPorTemporada = entrada.getElemento().getEpisodiosPorTemporada();
      this.elementoTotalUnidades = entrada.getElemento().getTotalUnidades();
      this.elementoTotalCapitulosLibro = entrada.getElemento().getTotalCapitulosLibro();
      this.elementoTotalPaginasLibro = entrada.getElemento().getTotalPaginasLibro();

      String rawDuracion = entrada.getElemento().getDuracion();
      if (rawDuracion != null
          && rawDuracion.length() >= 8
          && rawDuracion.matches("\\d{2}:\\d{2}:\\d{2}")) {
        // Only truncate if it matches the long time format HH:mm:ss
        this.elementoDuracion = rawDuracion.substring(0, 5);
      } else {
        // If it's short (e.g., "120m", "2:30") or another format, leave it as is
        this.elementoDuracion = rawDuracion;
      }

      this.elementoGeneros =
          entrada.getElemento().getGeneros() != null
              ? entrada.getElemento().getGeneros().stream()
                  .map(Genero::getNombre)
                  .collect(Collectors.joining(", "))
              : null;

      if (entrada.getElemento().getTipo() != null) {
        this.elementoTipoNombre = entrada.getElemento().getTipo().getNombre();
      }
    }

    if (entrada.getUsuario() != null) {
      this.usuarioId = entrada.getUsuario().getId();
    }
  }

  // Getters
  public Long getId() {
    return id;
  }

  public EstadoPersonal getEstadoPersonal() {
    return estadoPersonal;
  }

  public LocalDateTime getAgregadoEn() {
    return agregadoEn;
  }

  public Boolean getEsFavorito() {
    return esFavorito;
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

  public String getNotas() {
    return notas;
  }

  public void setNotas(String notas) {
    this.notas = notas;
  }

  public String getElementoDuracion() {
    return elementoDuracion;
  }

  public void setElementoDuracion(String elementoDuracion) {
    this.elementoDuracion = elementoDuracion;
  }

  public String getElementoGeneros() {
    return elementoGeneros;
  }

  public void setElementoGeneros(String elementoGeneros) {
    this.elementoGeneros = elementoGeneros;
  }
}
