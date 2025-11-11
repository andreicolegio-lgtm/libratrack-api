package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

/**
 * DTO para RECIBIR las ediciones de un Moderador ANTES de aprobar
 * una propuesta (Implementa la petición 'd' del Sprint 2).
 * --- ¡ACTUALIZADO (Sprint 2 / V2)! ---
 */
public class PropuestaUpdateDTO {

    // --- Campos Editables ---
    @NotBlank
    @Size(max = 255)
    private String tituloSugerido;

    @NotBlank
    @Size(max = 5000)
    private String descripcionSugerida;
    
    @NotBlank
    private String tipoSugerido; 
    
    @NotBlank
    private String generosSugeridos; 
    
    // --- ¡CAMPOS DE PROGRESO REFACTORIZADOS! (Petición b, c, d) ---
    
    @Size(max = 255)
    private String episodiosPorTemporada; // Para Series (ej. "10,8,12")

    @Min(value = 1)
    private Integer totalUnidades; // Para Anime / Manga

    @Min(value = 1)
    private Integer totalCapitulosLibro; // Para Libros

    @Min(value = 1)
    private Integer totalPaginasLibro; // Para Libros

    // --- CAMPOS ANTIGUOS (ELIMINADOS) ---
    // private Integer totalTemporadas;
    // private Boolean esUnidadUnica;
    // private Integer totalCapitulos;
    // private Integer totalPaginas;

    // --- Getters y Setters ---

    public String getTituloSugerido() { return tituloSugerido; }
    public void setTituloSugerido(String tituloSugerido) { this.tituloSugerido = tituloSugerido; }
    public String getDescripcionSugerida() { return descripcionSugerida; }
    public void setDescripcionSugerida(String descripcionSugerida) { this.descripcionSugerida = descripcionSugerida; }
    public String getTipoSugerido() { return tipoSugerido; }
    public void setTipoSugerido(String tipoSugerido) { this.tipoSugerido = tipoSugerido; }
    public String getGenerosSugeridos() { return generosSugeridos; }
    public void setGenerosSugeridos(String generosSugeridos) { this.generosSugeridos = generosSugeridos; }
    
    public String getEpisodiosPorTemporada() { return episodiosPorTemporada; }
    public void setEpisodiosPorTemporada(String episodiosPorTemporada) { this.episodiosPorTemporada = episodiosPorTemporada; }
    public Integer getTotalUnidades() { return totalUnidades; }
    public void setTotalUnidades(Integer totalUnidades) { this.totalUnidades = totalUnidades; }
    public Integer getTotalCapitulosLibro() { return totalCapitulosLibro; }
    public void setTotalCapitulosLibro(Integer totalCapitulosLibro) { this.totalCapitulosLibro = totalCapitulosLibro; }
    public Integer getTotalPaginasLibro() { return totalPaginasLibro; }
    public void setTotalPaginasLibro(Integer totalPaginasLibro) { this.totalPaginasLibro = totalPaginasLibro; }
}