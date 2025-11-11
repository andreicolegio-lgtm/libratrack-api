// Archivo: src/main/java/com/libratrack/api/service/PropuestaElementoService.java
package com.libratrack.api.service;

import com.libratrack.api.dto.ElementoResponseDTO;
import com.libratrack.api.dto.PropuestaRequestDTO;
import com.libratrack.api.dto.PropuestaResponseDTO;
import com.libratrack.api.dto.PropuestaUpdateDTO; 
import com.libratrack.api.entity.*;
import com.libratrack.api.exception.ConflictException; 
import com.libratrack.api.exception.ResourceNotFoundException; 
import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPropuesta;
import com.libratrack.api.model.EstadoPublicacion;
import com.libratrack.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para la lógica de negocio de la cola de moderación.
 * --- ¡ACTUALIZADO (Sprint 2 / V2)! ---
 */
@Service
public class PropuestaElementoService {

    @Autowired private PropuestaElementoRepository propuestaRepo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private ElementoRepository elementoRepo;
    @Autowired private TipoRepository tipoRepository;
    @Autowired private GeneroRepository generoRepository;

    /**
     * Crea una nueva propuesta y la añade a la cola de moderación (RF13).
     * --- ¡ACTUALIZADO (Sprint 2 / V2)! ---
     */
    @Transactional
    public PropuestaResponseDTO createPropuesta(PropuestaRequestDTO dto, String proponenteUsername) { 
        Usuario proponente = usuarioRepo.findByUsername(proponenteUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario proponente no encontrado.")); 
        
        PropuestaElemento nuevaPropuesta = new PropuestaElemento();
        nuevaPropuesta.setProponente(proponente);
        nuevaPropuesta.setTituloSugerido(dto.getTituloSugerido());
        nuevaPropuesta.setDescripcionSugerida(dto.getDescripcionSugerida());
        nuevaPropuesta.setTipoSugerido(dto.getTipoSugerido());
        nuevaPropuesta.setGenerosSugeridos(dto.getGenerosSugeridos());
        
        // --- ¡CAMPOS DE PROGRESO REFACTORIZADOS! ---
        nuevaPropuesta.setEpisodiosPorTemporada(dto.getEpisodiosPorTemporada());
        nuevaPropuesta.setTotalUnidades(dto.getTotalUnidades());
        nuevaPropuesta.setTotalCapitulosLibro(dto.getTotalCapitulosLibro());
        nuevaPropuesta.setTotalPaginasLibro(dto.getTotalPaginasLibro());

        PropuestaElemento propuestaGuardada = propuestaRepo.save(nuevaPropuesta);
        
        return new PropuestaResponseDTO(propuestaGuardada);
    }

    /**
     * Obtiene la lista de propuestas por un estado específico (RF14).
     */
    @Transactional(readOnly = true) 
    public List<PropuestaResponseDTO> getPropuestasPorEstado(EstadoPropuesta estado) {
        List<PropuestaElemento> propuestas = propuestaRepo.findByEstadoPropuesta(estado);
        return propuestas.stream()
                .map(PropuestaResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Aprueba una propuesta (RF15).
     * --- ¡REFACTORIZADO (Sprint 2 / V2)! ---
     */
    @Transactional
    public ElementoResponseDTO aprobarPropuesta(Long propuestaId, Long revisorId, PropuestaUpdateDTO dto) { 
        
        Usuario revisor = usuarioRepo.findById(revisorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario revisor no encontrado.")); 
        PropuestaElemento propuesta = propuestaRepo.findById(propuestaId)
                .orElseThrow(() -> new ResourceNotFoundException("Propuesta no encontrada con id: " + propuestaId)); 
        if (propuesta.getEstadoPropuesta() != EstadoPropuesta.PENDIENTE) {
            throw new ConflictException("Esta propuesta ya ha sido gestionada."); 
        }
        
        // 4. Actualizamos la propuesta con las ediciones del moderador (del DTO)
        updatePropuestaFields(propuesta, dto); // <-- ¡MÉTODO HELPER ACTUALIZADO!
        
        // 5. Lógica de "Traducción"
        Tipo tipoFinal = traducirTipo(propuesta.getTipoSugerido());
        Set<Genero> generosFinales = traducirGeneros(propuesta.getGenerosSugeridos());
        
        // 6. Crear el nuevo Elemento
        Elemento nuevoElemento = new Elemento();
        nuevoElemento.setTitulo(propuesta.getTituloSugerido());
        nuevoElemento.setDescripcion(propuesta.getDescripcionSugerida());
        nuevoElemento.setCreador(propuesta.getProponente());
        nuevoElemento.setTipo(tipoFinal);
        nuevoElemento.setGeneros(generosFinales);
        nuevoElemento.setEstadoContenido(EstadoContenido.COMUNITARIO);
        nuevoElemento.setEstadoPublicacion(EstadoPublicacion.DISPONIBLE); 

        // --- ¡CAMPOS DE PROGRESO REFACTORIZADOS! ---
        // Copiamos los datos de progreso total de la Propuesta al Elemento
        nuevoElemento.setEpisodiosPorTemporada(propuesta.getEpisodiosPorTemporada());
        nuevoElemento.setTotalUnidades(propuesta.getTotalUnidades());
        nuevoElemento.setTotalCapitulosLibro(propuesta.getTotalCapitulosLibro());
        nuevoElemento.setTotalPaginasLibro(propuesta.getTotalPaginasLibro());

        // 7. Actualizar la propuesta como "APROBADA"
        propuesta.setEstadoPropuesta(EstadoPropuesta.APROBADO);
        propuesta.setRevisor(revisor);
        propuestaRepo.save(propuesta); // Guardamos la propuesta (con campos editados y estado APROBADO)

        // 8. Guardar el nuevo elemento
        Elemento elementoGuardado = elementoRepo.save(nuevoElemento);
        
        // 9. Devolver el DTO de Respuesta
        return new ElementoResponseDTO(elementoGuardado);
    }
    
    /**
     * Método helper para actualizar una PropuestaElemento con datos de un DTO.
     * --- ¡ACTUALIZADO (Sprint 2 / V2)! ---
     */
    private void updatePropuestaFields(PropuestaElemento propuesta, PropuestaUpdateDTO dto) {
        propuesta.setTituloSugerido(dto.getTituloSugerido());
        propuesta.setDescripcionSugerida(dto.getDescripcionSugerida());
        propuesta.setTipoSugerido(dto.getTipoSugerido());
        propuesta.setGenerosSugeridos(dto.getGenerosSugeridos());
        
        // --- ¡CAMPOS DE PROGRESO REFACTORIZADOS! ---
        propuesta.setEpisodiosPorTemporada(dto.getEpisodiosPorTemporada());
        propuesta.setTotalUnidades(dto.getTotalUnidades());
        propuesta.setTotalCapitulosLibro(dto.getTotalCapitulosLibro());
        propuesta.setTotalPaginasLibro(dto.getTotalPaginasLibro());
    }
    
    // ... (Métodos traducirTipo y traducirGeneros sin cambios) ...
    private Tipo traducirTipo(String tipoSugerido) {
        if (tipoSugerido == null || tipoSugerido.isBlank()) {
            throw new ConflictException("El Tipo sugerido no puede estar vacío."); 
        }
        return tipoRepository.findByNombre(tipoSugerido)
                .orElseGet(() -> tipoRepository.save(new Tipo(tipoSugerido)));
    }
    
    private Set<Genero> traducirGeneros(String generosSugeridosString) {
        if (generosSugeridosString == null || generosSugeridosString.isBlank()) {
            throw new ConflictException("Los Géneros sugeridos no pueden estar vacíos."); 
        }
        Set<Genero> generosFinales = new HashSet<>();
        String[] generosSugeridosArray = generosSugeridosString.split("\\s*,\\s*");
        for (String nombreGenero : generosSugeridosArray) {
            if (nombreGenero.isBlank()) continue; 
            Genero genero = generoRepository.findByNombre(nombreGenero)
                    .orElseGet(() -> generoRepository.save(new Genero(nombreGenero)));
            generosFinales.add(genero);
        }
        if (generosFinales.isEmpty()) {
             throw new ConflictException("Se debe proporcionar al menos un género válido."); 
        }
        return generosFinales;
    }
}