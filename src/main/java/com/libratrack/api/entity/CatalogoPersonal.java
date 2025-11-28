package com.libratrack.api.entity;

import com.libratrack.api.model.EstadoPersonal;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa una entrada en la biblioteca personal de un usuario.
 *
 * <p>Esta entidad actúa como una tabla de relación extendida entre {@link Usuario} y {@link
 * Elemento}. Además de vincularlos, almacena el estado de consumo (ej. "Viendo", "Terminado"), el
 * progreso actual (temporada, capítulo, página) y si es un favorito.
 */
@Entity
@Table(
    name = "catalogo_personal",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {"usuario_id", "elemento_id"},
          name = "uk_catalogo_usuario_elemento")
    })
public class CatalogoPersonal {

  // =============================================================================================
  // IDENTIFICADOR
  // =============================================================================================

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // =============================================================================================
  // RELACIONES (CLAVE COMPUESTA LÓGICA)
  // =============================================================================================

  /** El usuario propietario de esta entrada en el catálogo. */
  @NotNull(message = "{validation.catalogo.usuario.required}")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  /** El elemento (libro, serie, juego) que se está rastreando. */
  @NotNull(message = "{validation.catalogo.elemento.required}")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "elemento_id", nullable = false)
  private Elemento elemento;

  // =============================================================================================
  // ESTADO Y METADATOS
  // =============================================================================================

  /** Estado actual del consumo (ej. PENDIENTE, EN_PROGRESO, TERMINADO). */
  @NotNull(message = "{validation.catalogo.estado.required}")
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private EstadoPersonal estadoPersonal;

  /** Fecha y hora en la que se añadió el elemento al catálogo personal. */
  @Column(nullable = false, updatable = false)
  private LocalDateTime agregadoEn;

  /** Indica si el usuario ha marcado este elemento como favorito. */
  @Column(nullable = false)
  private Boolean esFavorito = false;

  /** Notas personales del usuario sobre el elemento. */
  @Column(length = 2000)
  private String notas;

  // =============================================================================================
  // SEGUIMIENTO DE PROGRESO
  // =============================================================================================

  /** Número de la temporada actual (para Series/Anime). El valor mínimo lógico es 1. */
  @Min(value = 1, message = "{validation.catalogo.temporada.min}")
  private Integer temporadaActual;

  /** Unidad actual (ej. Volumen para mangas). */
  @Min(value = 0, message = "{validation.catalogo.unidad.min}")
  private Integer unidadActual;

  /** Capítulo actual (ej. Episodio de serie o Capítulo de libro). */
  @Min(value = 0, message = "{validation.catalogo.capitulo.min}")
  private Integer capituloActual;

  /** Página actual (Específico para Libros). */
  @Min(value = 0, message = "{validation.catalogo.pagina.min}")
  private Integer paginaActual;

  // =============================================================================================
  // CONSTRUCTORES Y EVENTOS DEL CICLO DE VIDA
  // =============================================================================================

  public CatalogoPersonal() {
    this.temporadaActual = 1;
    this.unidadActual = 0;
    this.capituloActual = 0;
    this.paginaActual = 0;
    this.esFavorito = false;
  }

  /**
   * Asigna valores por defecto antes de persistir la entidad en la base de datos si estos no fueron
   * proporcionados.
   */
  @PrePersist
  protected void onCreate() {
    if (this.agregadoEn == null) {
      this.agregadoEn = LocalDateTime.now();
    }
    // Aseguramos nulos como 0 o 1 para evitar NullPointerException en lógica de negocio
    if (this.temporadaActual == null) this.temporadaActual = 1;
    if (this.unidadActual == null) this.unidadActual = 0;
    if (this.capituloActual == null) this.capituloActual = 0;
    if (this.paginaActual == null) this.paginaActual = 0;
    if (this.esFavorito == null) this.esFavorito = false;
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

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public Elemento getElemento() {
    return elemento;
  }

  public void setElemento(Elemento elemento) {
    this.elemento = elemento;
  }

  public EstadoPersonal getEstadoPersonal() {
    return estadoPersonal;
  }

  public void setEstadoPersonal(EstadoPersonal estadoPersonal) {
    this.estadoPersonal = estadoPersonal;
  }

  public LocalDateTime getAgregadoEn() {
    return agregadoEn;
  }

  public void setAgregadoEn(LocalDateTime agregadoEn) {
    this.agregadoEn = agregadoEn;
  }

  public Integer getTemporadaActual() {
    return temporadaActual;
  }

  public void setTemporadaActual(Integer temporadaActual) {
    this.temporadaActual = temporadaActual;
  }

  public Integer getUnidadActual() {
    return unidadActual;
  }

  public void setUnidadActual(Integer unidadActual) {
    this.unidadActual = unidadActual;
  }

  public Integer getCapituloActual() {
    return capituloActual;
  }

  public void setCapituloActual(Integer capituloActual) {
    this.capituloActual = capituloActual;
  }

  public Integer getPaginaActual() {
    return paginaActual;
  }

  public void setPaginaActual(Integer paginaActual) {
    this.paginaActual = paginaActual;
  }

  public Boolean getEsFavorito() {
    return esFavorito;
  }

  public void setEsFavorito(Boolean esFavorito) {
    this.esFavorito = esFavorito;
  }

  public String getNotas() {
    return notas;
  }

  public void setNotas(String notas) {
    this.notas = notas;
  }

  // =============================================================================================
  // EQUALS, HASHCODE & TOSTRING
  // =============================================================================================

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CatalogoPersonal that = (CatalogoPersonal) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "CatalogoPersonal{"
        + "id="
        + id
        + ", estadoPersonal="
        + estadoPersonal
        + ", agregadoEn="
        + agregadoEn
        + ", esFavorito="
        + esFavorito
        + ", progreso=[T:"
        + temporadaActual
        + ", U:"
        + unidadActual
        + ", C:"
        + capituloActual
        + ", P:"
        + paginaActual
        + "]"
        + '}';
  }
}
