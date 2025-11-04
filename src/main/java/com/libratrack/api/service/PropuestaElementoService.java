package com.libratrack.api.service;

import com.libratrack.api.dto.PropuestaRequestDTO;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Genero;
import com.libratrack.api.entity.PropuestaElemento;
import com.libratrack.api.entity.Tipo;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPropuesta;
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.GeneroRepository;
import com.libratrack.api.repository.PropuestaElementoRepository;
import com.libratrack.api.repository.TipoRepository;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ¡Importante!

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PropuestaElementoService {

    @Autowired
    private PropuestaElementoRepository propuestaRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ElementoRepository elementoRepo;

    @Autowired
    private TipoRepository tipoRepository;

    @Autowired
    private GeneroRepository generoRepository;

    /**
     * Crea una nueva propuesta y la añade a la cola de moderación (RF13).
     *
     * @param dto El DTO con los datos sugeridos.
     * @param proponenteId El ID del usuario (extraído del token JWT) que hace la propuesta.
     * @return La PropuestaElemento guardada.
     */
    public PropuestaElemento createPropuesta(PropuestaRequestDTO dto, String proponenteUsername) throws Exception {
        // 1. Buscar al usuario proponente por su nombre (del token)
        Usuario proponente = usuarioRepo.findByUsername(proponenteUsername)
                .orElseThrow(() -> new Exception("Usuario proponente no encontrado."));

        // 2. Crear la nueva entidad Propuesta
        PropuestaElemento nuevaPropuesta = new PropuestaElemento();
        nuevaPropuesta.setProponente(proponente);
        nuevaPropuesta.setTituloSugerido(dto.getTituloSugerido());
        nuevaPropuesta.setDescripcionSugerida(dto.getDescripcionSugerida());
        nuevaPropuesta.setTipoSugerido(dto.getTipoSugerido());
        nuevaPropuesta.setGenerosSugeridos(dto.getGenerosSugeridos());
        
        // El estado por defecto es PENDIENTE (definido en la entidad)

        // 3. Guardar en la tabla de "sala de espera"
        return propuestaRepo.save(nuevaPropuesta);
    }

    /**
     * Obtiene la lista de propuestas pendientes (RF14 - Panel de Moderación).
     * @return Lista de propuestas con estado PENDIENTE.
     */
    public List<PropuestaElemento> getPropuestasPendientes() {
        return propuestaRepo.findByEstadoPropuesta(EstadoPropuesta.PENDIENTE);
    }


    /**
     * Aprueba una propuesta (RF15).
     * Esto "traduce" los strings de la propuesta, busca o crea las entidades
     * Tipo/Genero, y copia los datos a la tabla 'elementos'.
     *
     * @param propuestaId El ID de la propuesta a aprobar.
     * @param revisorId El ID del moderador (del token JWT) que aprueba.
     * @return El nuevo Elemento creado.
     */
    @Transactional // Asegura que si algo falla, no se guarde nada
    public Elemento aprobarPropuesta(Long propuestaId, Long revisorId) throws Exception {
        
        // 1. Buscar al moderador (revisor)
        Usuario revisor = usuarioRepo.findById(revisorId)
                .orElseThrow(() -> new Exception("Usuario revisor no encontrado."));
        // TO DO: Validar que el revisor tiene rol "ROLE_MODERADOR"

        // 2. Buscar la propuesta
        PropuestaElemento propuesta = propuestaRepo.findById(propuestaId)
                .orElseThrow(() -> new Exception("Propuesta no encontrada."));

        if (propuesta.getEstadoPropuesta() != EstadoPropuesta.PENDIENTE) {
            throw new Exception("Esta propuesta ya ha sido gestionada.");
        }

        // 3. --- INICIO DE LA LÓGICA DE "TRADUCCIÓN" ---

        // 3a. Traducir el TIPO (Buscar o Crear)
        String tipoSugerido = propuesta.getTipoSugerido();
        if (tipoSugerido == null || tipoSugerido.isBlank()) {
            throw new Exception("El Tipo sugerido no puede estar vacío.");
        }
        Tipo tipoFinal = tipoRepository.findByNombre(tipoSugerido)
                .orElseGet(() -> {
                    // No existe, así que lo creamos
                    return tipoRepository.save(new Tipo(tipoSugerido));
                });

        // 3b. Traducir los GÉNEROS (Buscar o Crear)
        Set<Genero> generosFinales = new HashSet<>();
        String generosSugeridosString = propuesta.getGenerosSugeridos();
        if (generosSugeridosString == null || generosSugeridosString.isBlank()) {
            throw new Exception("Los Géneros sugeridos no pueden estar vacíos.");
        }
        
        // Asumimos que los géneros vienen separados por coma (ej. "Aventuras, Fantasía")
        String[] generosSugeridos = generosSugeridosString.split("\\s*,\\s*"); // Separa por comas
        
        for (String nombreGenero : generosSugeridos) {
            if (nombreGenero.isBlank()) continue; // Ignora géneros vacíos si hay comas extra
            
            Genero genero = generoRepository.findByNombre(nombreGenero)
                    .orElseGet(() -> {
                        // No existe, lo creamos
                        return generoRepository.save(new Genero(nombreGenero));
                    });
            generosFinales.add(genero);
        }
        
        if (generosFinales.isEmpty()) {
             throw new Exception("Se debe proporcionar al menos un género válido.");
        }

        // --- FIN DE LA LÓGICA DE "TRADUCCIÓN" ---

        // 4. Crear el nuevo Elemento con los datos "traducidos"
        Elemento nuevoElemento = new Elemento();
        nuevoElemento.setTitulo(propuesta.getTituloSugerido());
        nuevoElemento.setDescripcion(propuesta.getDescripcionSugerida());
        nuevoElemento.setCreador(propuesta.getProponente()); // Asignamos el proponente original
        
        // Asignamos las entidades "traducidas"
        nuevoElemento.setTipo(tipoFinal);
        nuevoElemento.setGeneros(generosFinales);
        
        // (RF16) El contenido aprobado se marca como COMUNITARIO por defecto
        nuevoElemento.setEstadoContenido(EstadoContenido.COMUNITARIO);

        // 5. Actualizar la propuesta como "APROBADA"
        propuesta.setEstadoPropuesta(EstadoPropuesta.APROBADO);
        propuesta.setRevisor(revisor);
        propuestaRepo.save(propuesta);

        // 6. Guardar el nuevo elemento en la tabla principal
        return elementoRepo.save(nuevoElemento);
    }
    
    // (Añadiremos rechazarPropuesta() más tarde)
}