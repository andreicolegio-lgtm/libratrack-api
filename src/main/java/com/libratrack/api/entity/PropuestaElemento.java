package com.libratrack.api.entity;

import com.libratrack.api.model.EstadoPropuesta;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa una solicitud de un usuario para añadir un nuevo contenido a la plataforma.
 *
 * <p>Estas propuestas actúan como un borrador temporal. Los moderadores o administradores deben
 * revisarlas. Si se aprueban, los datos de esta entidad se utilizan para crear un nuevo registro en
 * la tabla {@link Elemento}.
 */
@Entity
@Table(name = "propuestas_elementos")
public class PropuestaElemento {

  // =============================================================================================
  // IDENTIFICADOR
  // =============================================================================================

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // =============================================================================================
  // ACTORES DEL PROCESO
  // =============================================================================================

  /** Usuario que envía la sugerencia. */
  @NotNull(message = "{validation.propuesta.proponente.required}")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "proponente_id", nullable = false)
  private Usuario proponente;

  /** Moderador o Admin que revisó la propuesta (puede ser null si está PENDIENTE). */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "revisor_id")
  private Usuario revisor;

  // =============================================================================================
  // DATOS SUGERIDOS (CONTENIDO)
  // =============================================================================================

  /** Título propuesto para el elemento. */
  @NotBlank(message = "{validation.propuesta.titulo.required}")
  @Size(max = 255, message = "{validation.propuesta.titulo.size}")
  @Column(nullable = false)
  private String tituloSugerido;

  /** Descripción o sinopsis sugerida. */
  @NotBlank(message = "{validation.propuesta.descripcion.required}")
  @Lob
  @Column(columnDefinition = "TEXT")
  private String descripcionSugerida;

  /**
   * Nombre del tipo sugerido (ej. "Anime", "Libro"). Se guarda como texto simple para facilitar la
   * edición antes de vincular a una entidad Tipo real.
   */
  @NotBlank(message = "{validation.propuesta.tipo.required}")
  @Size(max = 100, message = "{validation.propuesta.tipo.size}")
  private String tipoSugerido;

  /**
   * Lista de géneros sugeridos separados por comas (ej. "Acción, Aventura"). Se procesará para
   * vincular entidades Genero al aprobar.
   */
  @NotBlank(message = "{validation.propuesta.generos.required}")
  @Size(max = 255, message = "{validation.propuesta.generos.size}")
  private String generosSugeridos;

  @Column(length = 255)
  private String urlImagen;

  @Column(length = 50)
  private String duracion;

  // =============================================================================================
  // DETALLES TÉCNICOS SUGERIDOS
  // =============================================================================================

  @Size(max = 255)
  private String episodiosPorTemporada;

  @Min(value = 1, message = "{validation.propuesta.unidades.min}")
  private Integer totalUnidades;

  @Min(value = 1, message = "{validation.propuesta.capitulos.min}")
  private Integer totalCapitulosLibro;

  @Min(value = 1, message = "{validation.propuesta.paginas.min}")
  private Integer totalPaginasLibro;

  // =============================================================================================
  // ESTADO DE LA REVISIÓN
  // =============================================================================================

  @NotNull(message = "{validation.propuesta.estado.required}")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoPropuesta estadoPropuesta = EstadoPropuesta.PENDIENTE;

  /** Feedback del moderador en caso de rechazo o notas sobre la aprobación. */
  @Size(max = 500, message = "{validation.propuesta.comentarios.size}")
  private String comentariosRevision;

  @Column(nullable = false, updatable = false)
  private LocalDateTime fechaPropuesta;

  // =============================================================================================
  // CONSTRUCTORES Y EVENTOS
  // =============================================================================================

  public PropuestaElemento() {}

  @PrePersist
  protected void onCrear() {
    this.fechaPropuesta = LocalDateTime.now();
    if (this.estadoPropuesta == null) {
      this.estadoPropuesta = EstadoPropuesta.PENDIENTE;
    }
  }

  // =============================================================================================
  // GETTERS Y SETTERS
  // =============================================================================================

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getProponente() {
    return proponente;
  }

  public void setProponente(Usuario proponente) {
    this.proponente = proponente;
  }

  public Usuario getRevisor() {
    return revisor;
  }

  public void setRevisor(Usuario revisor) {
    this.revisor = revisor;
  }

  public String getTituloSugerido() {
    return tituloSugerido;
  }

  public void setTituloSugerido(String tituloSugerido) {
    this.tituloSugerido = tituloSugerido;
  }

  public String getDescripcionSugerida() {
    return descripcionSugerida;
  }

  public void setDescripcionSugerida(String descripcionSugerida) {
    this.descripcionSugerida = descripcionSugerida;
  }

  public String getTipoSugerido() {
    return tipoSugerido;
  }

  public void setTipoSugerido(String tipoSugerido) {
    this.tipoSugerido = tipoSugerido;
  }

  public String getGenerosSugeridos() {
    return generosSugeridos;
  }

  public void setGenerosSugeridos(String generosSugeridos) {
    this.generosSugeridos = generosSugeridos;
  }

  public String getUrlImagen() {
    return urlImagen;
  }

  public void setUrlImagen(String urlImagen) {
    this.urlImagen = urlImagen;
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

  public EstadoPropuesta getEstadoPropuesta() {
    return estadoPropuesta;
  }

  public void setEstadoPropuesta(EstadoPropuesta estadoPropuesta) {
    this.estadoPropuesta = estadoPropuesta;
  }

  public String getComentariosRevision() {
    return comentariosRevision;
  }

  public void setComentariosRevision(String comentariosRevision) {
    this.comentariosRevision = comentariosRevision;
  }

  public LocalDateTime getFechaPropuesta() {
    return fechaPropuesta;
  }

  public void setFechaPropuesta(LocalDateTime fechaPropuesta) {
    this.fechaPropuesta = fechaPropuesta;
  }

  public String getDuracion() {
    return duracion;
  }

  public void setDuracion(String duracion) {
    this.duracion = duracion;
  }

  // =============================================================================================
  // EQUALS, HASHCODE & TOSTRING
  // =============================================================================================

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PropuestaElemento that = (PropuestaElemento) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "PropuestaElemento{"
        + "id="
        + id
        + ", tituloSugerido='"
        + tituloSugerido
        + '\''
        + ", tipoSugerido='"
        + tipoSugerido
        + '\''
        + ", estadoPropuesta="
        + estadoPropuesta
        + '}';
  }
}
