package com.libratrack.api.entity;

import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPublicacion;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "elementos")
public class Elemento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String titulo;

  @Column(nullable = false, length = 5000)
  private String descripcion;

  @Column(length = 255)
  private String urlImagen;

  @Column private LocalDate fechaLanzamiento;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creador_id", nullable = true)
  private Usuario creador;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tipo_id", nullable = false)
  private Tipo tipo;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private EstadoContenido estadoContenido;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private EstadoPublicacion estadoPublicacion;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "elemento_genero",
      joinColumns = @JoinColumn(name = "elemento_id"),
      inverseJoinColumns = @JoinColumn(name = "genero_id"))
  private Set<Genero> generos = new HashSet<>();

  @Column(length = 255)
  private String episodiosPorTemporada;

  private Integer totalUnidades;
  private Integer totalCapitulosLibro;
  private Integer totalPaginasLibro;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "elemento_relaciones",
      joinColumns = @JoinColumn(name = "elemento_id"),
      inverseJoinColumns = @JoinColumn(name = "secuela_id"))
  private Set<Elemento> secuelas = new HashSet<>();

  @ManyToMany(mappedBy = "secuelas", fetch = FetchType.LAZY)
  private Set<Elemento> precuelas = new HashSet<>();

  private String duracion;

  public Elemento() {}

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
}
