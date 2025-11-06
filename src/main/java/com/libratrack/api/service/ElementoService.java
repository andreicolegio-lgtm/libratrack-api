package com.libratrack.api.service;

import com.libratrack.api.dto.ElementoDTO; 
import com.libratrack.api.dto.ElementoResponseDTO;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Genero;
import com.libratrack.api.entity.Tipo;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.model.EstadoContenido; 
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.GeneroRepository;
import com.libratrack.api.repository.TipoRepository;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para la lógica de negocio relacionada con la entidad Elemento.
 */
@Service
public class ElementoService {

    @Autowired
    private ElementoRepository elementoRepository;
    
    @Autowired
    private TipoRepository tipoRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ... (createElemento sin cambios) ...

    @Transactional 
    public ElementoResponseDTO createElemento(ElementoDTO dto) throws Exception {
        Tipo tipo = tipoRepository.findById(dto.getTipoId())
                .orElseThrow(() -> new Exception("Tipo no encontrado con id: " + dto.getTipoId()));

        Usuario creador = usuarioRepository.findById(dto.getCreadorId())
                .orElseThrow(() -> new Exception("Usuario creador no encontrado con id: " + dto.getCreadorId()));
        
        Set<Genero> generos = new HashSet<>(generoRepository.findAllById(dto.getGeneroIds()));
        if (generos.size() != dto.getGeneroIds().size() || generos.isEmpty()) {
            throw new Exception("Uno o más IDs de Género no son válidos o la lista está vacía.");
        }

        Elemento nuevoElemento = new Elemento();
        nuevoElemento.setTitulo(dto.getTitulo());
        nuevoElemento.setDescripcion(dto.getDescripcion());
        nuevoElemento.setFechaLanzamiento(dto.getFechaLanzamiento());
        nuevoElemento.setImagenPortadaUrl(dto.getImagenPortadaUrl());
        
        nuevoElemento.setTipo(tipo);
        nuevoElemento.setGeneros(generos);
        nuevoElemento.setCreador(creador); 
        nuevoElemento.setEstadoContenido(EstadoContenido.COMUNITARIO);

        Elemento elementoGuardado = elementoRepository.save(nuevoElemento);
        
        return new ElementoResponseDTO(elementoGuardado);
    }

    /**
     * REFACTORIZADO: Busca todos los elementos o filtra por 3 criterios (RF09).
     * * @param searchText El término de búsqueda opcional (por título).
     * @param tipoName El nombre del Tipo para filtrar (ej. "Serie").
     * @param generoName El nombre del Género para filtrar (ej. "Fantasía").
     * @return Una lista de DTOs de los Elementos que cumplen con los criterios.
     */
    public List<ElementoResponseDTO> findAllElementos(String searchText, String tipoName, String generoName) {
        
        // 1. Obtener TODOS los elementos de forma inicial
        List<Elemento> elementos = elementoRepository.findAll();
        if (elementos.isEmpty()) {
             return List.of();
        }
        
        // 2. Aplicar los 3 filtros secuencialmente (AND lógico)
        List<Elemento> elementosFiltrados = elementos.stream()
            
            // Filtro 1: Búsqueda por Título (searchText)
            .filter(e -> {
                if (searchText != null && !searchText.isBlank()) {
                    return e.getTitulo().toLowerCase().contains(searchText.toLowerCase());
                }
                return true;
            })
            
            // Filtro 2: Por Tipo (tipoName)
            .filter(e -> {
                if (tipoName != null && !tipoName.isBlank() && e.getTipo() != null) {
                    return e.getTipo().getNombre().equalsIgnoreCase(tipoName);
                }
                return true;
            })
            
            // Filtro 3: Por Género (generoName)
            .filter(e -> {
                if (generoName != null && !generoName.isBlank() && e.getGeneros() != null) {
                    // Comprueba si CUALQUIERA de los géneros del Elemento coincide
                    return e.getGeneros().stream()
                            .anyMatch(g -> g.getNombre().equalsIgnoreCase(generoName));
                }
                return true;
            })
            
            .collect(Collectors.toList());

        // 3. Mapea la lista filtrada de Entidades a una lista de DTOs
        return elementosFiltrados.stream()
                .map(ElementoResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca un elemento por su ID (RF10 - Ficha Detallada).
     * [Preservado]
     */
    public Optional<ElementoResponseDTO> findElementoById(Long id) {
        Optional<Elemento> elementoOptional = elementoRepository.findById(id);
        return elementoOptional.map(ElementoResponseDTO::new);
    }
}