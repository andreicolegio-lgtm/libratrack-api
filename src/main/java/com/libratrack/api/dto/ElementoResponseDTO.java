package com.libratrack.api.dto;

import com.libratrack.api.entity.Elemento;
import com.libratrack.api.model.EstadoContenido;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO para ENVIAR un Elemento al cliente (móvil) (RF10).
 * Resuelve la LazyInitializationException.
 */
public class ElementoResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaLanzamiento;
    private String imagenPortadaUrl;
    private EstadoContenido estadoContenido;

    // --- Relaciones Aplanadas ---
    private String tipo; // Solo el nombre del tipo (ej. "Serie")
    private Set<String> generos; // Solo los nombres (ej. ["Ciencia Ficción", "Drama"])
    private String creadorUsername; // Quién lo propuso

    // --- Constructor ---
    // Sabe cómo "mapear" la Entidad al DTO
    public ElementoResponseDTO(Elemento elemento) {
        this.id = elemento.getId();
        this.titulo = elemento.getTitulo();
        this.descripcion = elemento.getDescripcion();
        this.fechaLanzamiento = elemento.getFechaLanzamiento();
        this.imagenPortadaUrl = elemento.getImagenPortadaUrl();
        this.estadoContenido = elemento.getEstadoContenido();

        // Mapeo seguro (evita LazyInitialization)
        this.tipo = elemento.getTipo().getNombre();
        
        // Mapea el Set<Genero> a un Set<String>
        this.generos = elemento.getGeneros().stream()
                            .map(genero -> genero.getNombre())
                            .collect(Collectors.toSet());

        if (elemento.getCreador() != null) {
            this.creadorUsername = elemento.getCreador().getUsername();
        }
    }

    // --- Getters ---
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public LocalDate getFechaLanzamiento() { return fechaLanzamiento; }
    public String getImagenPortadaUrl() { return imagenPortadaUrl; }
    public EstadoContenido getEstadoContenido() { return estadoContenido; }
    public String getTipo() { return tipo; }
    public Set<String> getGeneros() { return generos; }
    public String getCreadorUsername() { return creadorUsername; }
}