package com.libratrack.api.entity;

import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPublicacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Representa un contenido audiovisual o literario dentro de la plataforma LibraTrack.
 *
 * <p>Esta entidad es el núcleo del sistema y puede representar diversos tipos de medios (Libros,
 * Películas, Series, Videojuegos, etc.) definidos por su {@link Tipo}. Gestiona tanto la
 * información descriptiva como las relaciones con otros elementos (secuelas/precuelas).
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "elementos")
public class Elemento {

  // =============================================================================================
  // IDENTIFICADOR
  // =============================================================================================

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // =============================================================================================
  // INFORMACIÓN BÁSICA
  // =============================================================================================

  /** Título oficial del elemento. */
  @NotBlank(message = "{validation.elemento.titulo.required}")
  @Size(max = 255, message = "{validation.elemento.titulo.size}")
  @Column(nullable = false, length = 255)
  private String titulo;

  /**
   * Sinopsis o descripción detallada del contenido. Se permite una longitud mayor (hasta 5000
   * caracteres) para descripciones extensas.
   */
  @Size(max = 5000, message = "{validation.elemento.descripcion.size}")
  @Column(nullable = true, length = 5000)
  private String descripcion;

  /**
   * URL pública de la imagen de portada o póster. Generalmente almacenada en Google Cloud Storage.
   */
  @Column(length = 255)
  private String urlImagen;

  /** Fecha de estreno, publicación o lanzamiento oficial. */
  @Column private LocalDate fechaLanzamiento;

  /**
   * Duración aproximada (ej. "120 min", "24 pag", "Unknown"). Se usa String para permitir
   * flexibilidad entre formatos de distintos medios.
   */
  @Column(length = 50)
  private String duracion;

  // =============================================================================================
  // RELACIONES PRINCIPALES
  // =============================================================================================

  /**
   * Usuario que creó este registro originalmente. Puede ser null si el usuario fue eliminado, pero
   * el contenido persiste.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creador_id", nullable = true)
  private Usuario creador;

  /** Categoría principal del elemento (Anime, Libro, Película, etc.). */
  @NotNull(message = "{validation.elemento.tipo.required}")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tipo_id", nullable = false)
  private Tipo tipo;

  /** Conjunto de géneros asociados (Acción, Aventura, RPG, etc.). */
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "elemento_genero",
      joinColumns = @JoinColumn(name = "elemento_id"),
      inverseJoinColumns = @JoinColumn(name = "genero_id"))
  private Set<Genero> generos = new HashSet<>();

  // =============================================================================================
  // ESTADOS Y METADATOS
  // =============================================================================================

  /**
   * Define si el contenido es OFICIAL (verificado por admins) o COMUNITARIO (creado por usuarios).
   */
  @NotNull(message = "{validation.elemento.estadoContenido.required}")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private EstadoContenido estadoContenido;

  /** Estado de la publicación (EN_EMISION, FINALIZADO, etc.). */
  @NotNull(message = "{validation.elemento.estadoPublicacion.required}")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private EstadoPublicacion estadoPublicacion;

  // =============================================================================================
  // DETALLES ESPECÍFICOS (SEGÚN TIPO)
  // =============================================================================================

  /**
   * Formato libre para describir la cantidad de episodios (ej. "12", "12+OVA", "24 (Parte 1)").
   * Principalmente para Series o Animes.
   */
  @Column(length = 255)
  private String episodiosPorTemporada;

  /** Cantidad total de unidades principales (ej. Temporadas para series, Volúmenes para mangas). */
  private Integer totalUnidades;

  /** Cantidad total de capítulos (ej. Capítulos de un libro o manga). */
  private Integer totalCapitulosLibro;

  /** Cantidad total de páginas (exclusivo para Libros). */
  private Integer totalPaginasLibro;

  // =============================================================================================
  // RELACIONES DE SECUELA / PRECUELA (Auto-referenciales)
  // =============================================================================================

  /** Elementos que cronológicamente siguen a este (Secuelas). Relación Dueña (Owner side). */
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "elemento_relaciones",
      joinColumns = @JoinColumn(name = "elemento_id"),
      inverseJoinColumns = @JoinColumn(name = "secuela_id"))
  private Set<Elemento> secuelas = new HashSet<>();

  /**
   * Elementos que cronológicamente preceden a este (Precuelas). Relación Inversa (Inverse side).
   */
  @ManyToMany(mappedBy = "secuelas", fetch = FetchType.LAZY)
  private Set<Elemento> precuelas = new HashSet<>();

  // =============================================================================================
  // CONSTRUCTORES
  // =============================================================================================

  public Elemento() {}

  // =============================================================================================
  // GETTERS Y SETTERS
  // =============================================================================================

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

  public Usuario getCreador() {
    return creador;
  }

  public void setCreador(Usuario creador) {
    this.creador = creador;
  }

  public Tipo getTipo() {
    return tipo;
  }

  public void setTipo(Tipo tipo) {
    this.tipo = tipo;
  }

  public EstadoContenido getEstadoContenido() {
    return estadoContenido;
  }

  public void setEstadoContenido(EstadoContenido estadoContenido) {
    this.estadoContenido = estadoContenido;
  }

  public Set<Genero> getGeneros() {
    return generos;
  }

  public void setGeneros(Set<Genero> generos) {
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

  public Set<Elemento> getSecuelas() {
    return secuelas;
  }

  public void setSecuelas(Set<Elemento> secuelas) {
    this.secuelas = secuelas;
  }

  public Set<Elemento> getPrecuelas() {
    return precuelas;
  }

  public void setPrecuelas(Set<Elemento> precuelas) {
    this.precuelas = precuelas;
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

  /**
   * Implementación de equals basada en la identidad JPA. Dos elementos son iguales si tienen el
   * mismo ID no nulo.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Elemento elemento = (Elemento) o;
    // Si el ID es null, la entidad aún no ha sido persistida y no es igual a ninguna otra
    return id != null && Objects.equals(id, elemento.id);
  }

  /**
   * Implementación consistente de hashCode para entidades JPA. Usamos una constante o el hashCode
   * de la clase para evitar cambios al persistir (cuando se asigna el ID).
   */
  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "Elemento{"
        + "id="
        + id
        + ", titulo='"
        + titulo
        + '\''
        + ", tipo="
        + (tipo != null ? tipo.getNombre() : "null")
        + ", estadoContenido="
        + estadoContenido
        + ", estadoPublicacion="
        + estadoPublicacion
        + '}';
  }
}
