package com.libratrack.api.dto;

import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Genero;
import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPublicacion;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO para ENVIAR los datos de un Elemento al cliente (Búsqueda/Detalle).
 * --- ¡ACTUALIZADO (Sprint 2 / V2)! ---
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
    private Set<String> generos;
    
    // --- ¡CAMPOS DE PROGRESO TOTAL REFACTORIZADOS! (Petición b, c, d) ---
    private EstadoPublicacion estadoPublicacion;
    private String episodiosPorTemporada; // Para Series
    private Integer totalUnidades;        // Para Anime / Manga
    private Integer totalCapitulosLibro;  // Para Libros
    private Integer totalPaginasLibro;    // Para Libros

    // --- CAMPOS ANTIGUOS (ELIMINADOS) ---
    // private Integer totalTemporadas;
    // private Boolean esUnidadUnica;
    // private Integer totalCapitulos;
    // private Integer totalPaginas;


    public ElementoResponseDTO(Elemento elemento) {
        this.id = elemento.getId();
        this.titulo = elemento.getTitulo();
        this.descripcion = elemento.getDescripcion();
        this.urlImagen = elemento.getUrlImagen();
        this.fechaLanzamiento = elemento.getFechaLanzamiento(); 
        this.tipoNombre = elemento.getTipo().getNombre(); 
        this.estadoContenido = elemento.getEstadoContenido();
        this.creadorUsername = elemento.getCreador() != null ? elemento.getCreador().getUsername() : "OFICIAL";
        
        this.generos = elemento.getGeneros().stream()
            .map(Genero::getNombre)
            .collect(Collectors.toSet());
            
        this.estadoPublicacion = elemento.getEstadoPublicacion();
        
        // --- Mapeo de Progreso (Refactorizado) ---
        this.episodiosPorTemporada = elemento.getEpisodiosPorTemporada();
        this.totalUnidades = elemento.getTotalUnidades();
        this.totalCapitulosLibro = elemento.getTotalCapitulosLibro();
        this.totalPaginasLibro = elemento.getTotalPaginasLibro();
    }

    // --- Getters ---
    // (Jackson los usa automáticamente)
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getUrlImagen() { return urlImagen; }
    public LocalDate getFechaLanzamiento() { return fechaLanzamiento; }
    public String getTipoNombre() { return tipoNombre; } 
    public EstadoContenido getEstadoContenido() { return estadoContenido; }
    public String getCreadorUsername() { return creadorUsername; }
    public Set<String> getGeneros() { return generos; }
    public EstadoPublicacion getEstadoPublicacion() { return estadoPublicacion; }
    
    public String getEpisodiosPorTemporada() { return episodiosPorTemporada; }
    public Integer getTotalUnidades() { return totalUnidades; }
    public Integer getTotalCapitulosLibro() { return totalCapitulosLibro; }
    public Integer getTotalPaginasLibro() { return totalPaginasLibro; }
}