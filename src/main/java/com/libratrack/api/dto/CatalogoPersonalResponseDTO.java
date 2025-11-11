package com.libratrack.api.dto;

import com.libratrack.api.entity.CatalogoPersonal;
import com.libratrack.api.model.EstadoPersonal;
import com.libratrack.api.model.EstadoPublicacion;
import java.time.LocalDateTime;

/**
 * DTO para ENVIAR una entrada del catálogo al cliente (móvil).
 * --- ¡ACTUALIZADO (Sprint 2 / V2)! ---
 */
public class CatalogoPersonalResponseDTO {

    private Long id;
    private EstadoPersonal estadoPersonal;
    private LocalDateTime agregadoEn;
    
    // --- Progreso Actual del Usuario (Campos existentes, ¡son correctos!) ---
    private Integer temporadaActual; 
    private Integer unidadActual;   // (Episodio / Cap Manga/Anime)
    private Integer capituloActual; // (Cap Libro)
    private Integer paginaActual;   // (Pág Libro)

    // --- Datos del Elemento Relacionado ---
    private Long elementoId;
    private String elementoTitulo;
    private String elementoTipoNombre; 
    private String elementoUrlImagen;

    // --- ¡PROGRESO TOTAL DEL ELEMENTO REFACTORIZADO! (Petición b, c, d) ---
    private EstadoPublicacion elementoEstadoPublicacion; 
    private String elementoEpisodiosPorTemporada; // Para Series
    private Integer elementoTotalUnidades;        // Para Anime / Manga
    private Integer elementoTotalCapitulosLibro;  // Para Libros
    private Integer elementoTotalPaginasLibro;    // Para Libros
    
    // --- CAMPOS ANTIGUOS (ELIMINADOS) ---
    // private Integer elementoTotalTemporadas;
    // private Boolean elementoEsUnidadUnica;
    // private Integer elementoTotalCapitulos;
    // private Integer elementoTotalPaginas;

    private Long usuarioId;

    // --- Constructor de Mapeo ---
    public CatalogoPersonalResponseDTO(CatalogoPersonal entrada) {
        this.id = entrada.getId();
        this.estadoPersonal = entrada.getEstadoPersonal();
        this.agregadoEn = entrada.getAgregadoEn();
        
        // Progreso Actual (Usuario)
        this.temporadaActual = entrada.getTemporadaActual(); 
        this.unidadActual = entrada.getUnidadActual();
        this.capituloActual = entrada.getCapituloActual(); 
        this.paginaActual = entrada.getPaginaActual();     
        
        // Datos del Elemento
        this.elementoId = entrada.getElemento().getId();
        this.elementoTitulo = entrada.getElemento().getTitulo();
        this.elementoTipoNombre = entrada.getElemento().getTipo().getNombre(); 
        this.elementoUrlImagen = entrada.getElemento().getUrlImagen();
        
        // Progreso Total (Elemento)
        this.elementoEstadoPublicacion = entrada.getElemento().getEstadoPublicacion();
        this.elementoEpisodiosPorTemporada = entrada.getElemento().getEpisodiosPorTemporada();
        this.elementoTotalUnidades = entrada.getElemento().getTotalUnidades();
        this.elementoTotalCapitulosLibro = entrada.getElemento().getTotalCapitulosLibro();
        this.elementoTotalPaginasLibro = entrada.getElemento().getTotalPaginasLibro();
        
        this.usuarioId = entrada.getUsuario().getId();
    }

    // --- Getters ---
    // (Jackson los usa automáticamente)
    public Long getId() { return id; }
    public EstadoPersonal getEstadoPersonal() { return estadoPersonal; }
    public LocalDateTime getAgregadoEn() { return agregadoEn; }
    public Integer getTemporadaActual() { return temporadaActual; }
    public Integer getUnidadActual() { return unidadActual; }
    public Integer getCapituloActual() { return capituloActual; }
    public Integer getPaginaActual() { return paginaActual; }
    public Long getElementoId() { return elementoId; }
    public String getElementoTitulo() { return elementoTitulo; }
    public String getElementoTipoNombre() { return elementoTipoNombre; }
    public String getElementoUrlImagen() { return elementoUrlImagen; }
    public EstadoPublicacion getElementoEstadoPublicacion() { return elementoEstadoPublicacion; }
    public String getElementoEpisodiosPorTemporada() { return elementoEpisodiosPorTemporada; }
    public Integer getElementoTotalUnidades() { return elementoTotalUnidades; }
    public Integer getElementoTotalCapitulosLibro() { return elementoTotalCapitulosLibro; }
    public Integer getElementoTotalPaginasLibro() { return elementoTotalPaginasLibro; }
    public Long getUsuarioId() { return usuarioId; }
}