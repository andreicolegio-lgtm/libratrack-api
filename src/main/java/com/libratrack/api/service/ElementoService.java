// Archivo: src/main/java/com/libratrack/api/service/ElementoService.java
package com.libratrack.api.service;

// --- ¡BLOQUE DE IMPORTACIÓN AÑADIDO! ---
import com.libratrack.api.dto.ElementoFormDTO; 
import com.libratrack.api.dto.ElementoResponseDTO;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Genero;
import com.libratrack.api.entity.Tipo;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ResourceNotFoundException; 
import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPublicacion;
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.Set;
// --- FIN DEL BLOQUE DE IMPORTACIÓN ---

/**
 * --- ¡ACTUALIZADO (Sprint 6)! ---
 */
@Service
public class ElementoService {
    
    @Autowired private ElementoRepository elementoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    
    @Autowired private PropuestaElementoService propuestaService; 
    
    /**
     * Busca todos los elementos (paginado) (RF09).
     */
    public Page<ElementoResponseDTO> findAllElementos(Pageable pageable, String searchText, String tipoName, String generoName) {
        Page<Elemento> paginaDeElementos = elementoRepository.findElementosByFiltros(
            searchText, 
            tipoName, 
            generoName, 
            pageable
        );
        return paginaDeElementos.map(ElementoResponseDTO::new);
    }
    
    /**
     * Busca un elemento por su ID (RF10).
     */
    public Optional<ElementoResponseDTO> findElementoById(Long id) {
        Optional<Elemento> elementoOptional = elementoRepository.findById(id);
        return elementoOptional.map(ElementoResponseDTO::new);
    }
    
    // --- ¡NUEVOS MÉTODOS DE ADMIN/MOD! ---
    
    /**
     * (Petición 15) Crea un nuevo Elemento directamente como OFICIAL.
     */
    @Transactional
    public ElementoResponseDTO crearElementoOficial(ElementoFormDTO dto, String adminUsername) {
        Usuario admin = usuarioRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Admin no encontrado."));
        
        Elemento nuevoElemento = new Elemento();
        
        mapElementoFromFormDTO(nuevoElemento, dto);
        
        nuevoElemento.setCreador(admin); 
        nuevoElemento.setEstadoContenido(EstadoContenido.OFICIAL); 
        nuevoElemento.setEstadoPublicacion(EstadoPublicacion.DISPONIBLE); 

        Elemento elementoGuardado = elementoRepository.save(nuevoElemento);
        return new ElementoResponseDTO(elementoGuardado);
    }

    /**
     * (Petición 8) Actualiza un Elemento existente.
     */
    @Transactional
    public ElementoResponseDTO updateElemento(Long elementoId, ElementoFormDTO dto) {
        Elemento elemento = elementoRepository.findById(elementoId)
                .orElseThrow(() -> new ResourceNotFoundException("Elemento no encontrado con id: " + elementoId));
        
        mapElementoFromFormDTO(elemento, dto);
        
        Elemento elementoGuardado = elementoRepository.save(elemento);
        return new ElementoResponseDTO(elementoGuardado);
    }
    
    /**
     * (Petición 17) Cambia el estado de un Elemento a OFICIAL.
     */
    @Transactional
    public ElementoResponseDTO oficializarElemento(Long elementoId) {
        Elemento elemento = elementoRepository.findById(elementoId)
                .orElseThrow(() -> new ResourceNotFoundException("Elemento no encontrado con id: " + elementoId));
        
        elemento.setEstadoContenido(EstadoContenido.OFICIAL);
        Elemento elementoGuardado = elementoRepository.save(elemento);
        
        return new ElementoResponseDTO(elementoGuardado);
    }
    
    /**
     * (Petición F) Cambia el estado de un Elemento a COMUNITARIO.
     * Solo para Admins.
     */
    @Transactional
    public ElementoResponseDTO comunitarizarElemento(Long elementoId) {
        Elemento elemento = elementoRepository.findById(elementoId)
                .orElseThrow(() -> new ResourceNotFoundException("Elemento no encontrado con id: " + elementoId));
        
        elemento.setEstadoContenido(EstadoContenido.COMUNITARIO); // <-- La lógica
        Elemento elementoGuardado = elementoRepository.save(elemento);
        
        return new ElementoResponseDTO(elementoGuardado);
    }
    
    
    /**
     * (Petición 8) Método helper para mapear los campos
     * del DTO a la entidad Elemento.
     */
    private void mapElementoFromFormDTO(Elemento elemento, ElementoFormDTO dto) {
        // Reutilizamos la lógica de traducción de PropuestaElementoService
        Tipo tipo = propuestaService.traducirTipo(dto.getTipoNombre());
        Set<Genero> generos = propuestaService.traducirGeneros(dto.getGenerosNombres());
        
        elemento.setTitulo(dto.getTitulo());
        elemento.setDescripcion(dto.getDescripcion());
        elemento.setUrlImagen(dto.getUrlImagen());
        elemento.setTipo(tipo);
        elemento.setGeneros(generos);
        
        // Mapeo de Progreso
        elemento.setEpisodiosPorTemporada(dto.getEpisodiosPorTemporada());
        elemento.setTotalUnidades(dto.getTotalUnidades());
        elemento.setTotalCapitulosLibro(dto.getTotalCapitulosLibro());
        elemento.setTotalPaginasLibro(dto.getTotalPaginasLibro());
    }
}