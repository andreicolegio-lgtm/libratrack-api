package com.libratrack.api.service;

import com.libratrack.api.dto.ElementoDTO;
import com.libratrack.api.dto.ElementoResponseDTO;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Genero;
import com.libratrack.api.entity.Tipo;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPublicacion;
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.GeneroRepository;
import com.libratrack.api.repository.TipoRepository;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// NUEVAS IMPORTACIONES
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
// import java.util.List; // Ya no es necesario para findAllElementos
import java.util.Optional;
import java.util.Set;
// import java.util.stream.Collectors; // Ya no es necesario para findAllElementos

@Service
public class ElementoService {
    
    // ... (Inyecciones de dependencias sin cambios) ...
    @Autowired private ElementoRepository elementoRepository;
    @Autowired private TipoRepository tipoRepository;
    @Autowired private GeneroRepository generoRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    // ... (createElemento sin cambios) ...
    @Transactional
    public ElementoResponseDTO createElemento(ElementoDTO dto) throws Exception {
        // ... (código existente)
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
        nuevoElemento.setUrlImagen(dto.getUrlImagen()); 
        nuevoElemento.setTipo(tipo);
        nuevoElemento.setGeneros(generos);
        nuevoElemento.setCreador(creador); 
        nuevoElemento.setEstadoContenido(EstadoContenido.COMUNITARIO);
        nuevoElemento.setEstadoPublicacion(EstadoPublicacion.DISPONIBLE); 
        Elemento elementoGuardado = elementoRepository.save(nuevoElemento);
        return new ElementoResponseDTO(elementoGuardado);
    }


    /**
     * Busca todos los elementos o filtra por 3 criterios (RF09).
     *
     * --- ¡REFACTORIZADO PARA PAGINACIÓN! ---
     * Ahora delega toda la lógica de filtrado y paginación a la base de datos
     * usando el método findElementosByFiltros del repositorio.
     *
     * @param pageable Objeto Pageable que viene del controlador.
     * @param searchText El término de búsqueda (título).
     * @param tipoName El nombre del tipo.
     * @param generoName El nombre del género.
     * @return Una 'Page' (página) de DTOs de respuesta.
     */
    public Page<ElementoResponseDTO> findAllElementos(Pageable pageable, String searchText, String tipoName, String generoName) {
        
        // 1. Llama al nuevo método del repositorio
        Page<Elemento> paginaDeElementos = elementoRepository.findElementosByFiltros(
            searchText, 
            tipoName, 
            generoName, 
            pageable
        );
        
        // 2. Convierte la 'Page<Elemento>' en una 'Page<ElementoResponseDTO>'
        // El método .map() de la 'Page' hace esto automáticamente.
        return paginaDeElementos.map(ElementoResponseDTO::new);
    }

    /**
     * Busca un elemento por su ID (RF10 - Ficha Detallada).
     */
    public Optional<ElementoResponseDTO> findElementoById(Long id) {
        Optional<Elemento> elementoOptional = elementoRepository.findById(id);
        return elementoOptional.map(ElementoResponseDTO::new);
    }
}