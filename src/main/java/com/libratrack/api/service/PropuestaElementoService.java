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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para gestionar el ciclo de vida de las propuestas de contenido (Creación, Revisión,
 * Aprobación).
 */
@Service
public class PropuestaElementoService {

  @Autowired private PropuestaElementoRepository propuestaRepo;
  @Autowired private UsuarioRepository usuarioRepo;
  @Autowired private ElementoRepository elementoRepo;
  @Autowired private TipoRepository tipoRepository;
  @Autowired private GeneroRepository generoRepository;
  @Autowired private DataSqlSyncService dataSqlSyncService;

  @Transactional
  public PropuestaResponseDTO createPropuesta(PropuestaRequestDTO dto, Long proponenteId) {
    Usuario proponente =
        usuarioRepo
            .findById(proponenteId)
            .orElseThrow(() -> new ResourceNotFoundException("{exception.user.not_found}"));

    PropuestaElemento nuevaPropuesta = new PropuestaElemento();
    nuevaPropuesta.setProponente(proponente);
    nuevaPropuesta.setTituloSugerido(dto.getTituloSugerido());
    nuevaPropuesta.setDescripcionSugerida(dto.getDescripcionSugerida());
    nuevaPropuesta.setTipoSugerido(dto.getTipoSugerido());
    nuevaPropuesta.setGenerosSugeridos(dto.getGenerosSugeridos());
    // La imagen puede ser opcional en la propuesta
    nuevaPropuesta.setUrlImagen(dto.getImagenPortadaUrl());

    nuevaPropuesta.setEpisodiosPorTemporada(dto.getEpisodiosPorTemporada());
    nuevaPropuesta.setTotalUnidades(dto.getTotalUnidades());
    nuevaPropuesta.setTotalCapitulosLibro(dto.getTotalCapitulosLibro());
    nuevaPropuesta.setTotalPaginasLibro(dto.getTotalPaginasLibro());
    nuevaPropuesta.setDuracion(dto.getDuracion());

    PropuestaElemento propuestaGuardada = propuestaRepo.save(nuevaPropuesta);
    return new PropuestaResponseDTO(propuestaGuardada);
  }

  @Transactional(readOnly = true)
  public List<PropuestaResponseDTO> getPropuestasPorEstado(
      EstadoPropuesta estado,
      String search,
      List<String> types,
      List<String> genres,
      String sortMode,
      boolean isAscending) {

    // Construir el objeto Sort basado en sortMode y isAscending
    Sort sort;
    if ("ALPHA".equalsIgnoreCase(sortMode)) {
      sort =
          isAscending
              ? Sort.by("tituloSugerido").ascending()
              : Sort.by("tituloSugerido").descending();
    } else { // Default to "DATE"
      sort =
          isAscending
              ? Sort.by("fechaPropuesta").ascending()
              : Sort.by("fechaPropuesta").descending();
    }

    // Convert genres list to a single comma-separated string
    String genresString = genres != null && !genres.isEmpty() ? String.join(",", genres) : null;

    // Llamar al repositorio con Pageable
    Pageable pageable = PageRequest.of(0, 20, sort); // Ejemplo: página 0, tamaño 20
    return propuestaRepo.searchPropuestas(estado, search, types, genresString, pageable).stream()
        .map(PropuestaResponseDTO::new)
        .collect(Collectors.toList());
  }

  @Transactional
  public ElementoResponseDTO aprobarPropuesta(
      Long propuestaId, Long revisorId, PropuestaUpdateDTO dto) {
    Usuario revisor =
        usuarioRepo
            .findById(revisorId)
            .orElseThrow(() -> new ResourceNotFoundException("{exception.user.not_found}"));

    PropuestaElemento propuesta =
        propuestaRepo
            .findById(propuestaId)
            .orElseThrow(() -> new ResourceNotFoundException("{exception.propuesta.not_found}"));

    if (propuesta.getEstadoPropuesta() != EstadoPropuesta.PENDIENTE) {
      throw new ConflictException("{exception.propuesta.already_handled}");
    }

    // Actualizar la propuesta con los datos finales editados por el moderador
    actualizarDatosPropuesta(propuesta, dto);

    // Procesar tipos y géneros (crear si no existen)
    Tipo tipoFinal = traducirTipo(propuesta.getTipoSugerido());
    Set<Genero> generosFinales = traducirGeneros(propuesta.getGenerosSugeridos(), tipoFinal);

    // Crear el Elemento final
    Elemento nuevoElemento = new Elemento();
    nuevoElemento.setTitulo(propuesta.getTituloSugerido());
    nuevoElemento.setDescripcion(propuesta.getDescripcionSugerida());
    nuevoElemento.setCreador(propuesta.getProponente());
    nuevoElemento.setTipo(tipoFinal);
    nuevoElemento.setGeneros(generosFinales);
    nuevoElemento.setEstadoContenido(EstadoContenido.COMUNITARIO);
    nuevoElemento.setEstadoPublicacion(EstadoPublicacion.AVAILABLE);
    nuevoElemento.setUrlImagen(propuesta.getUrlImagen());

    nuevoElemento.setEpisodiosPorTemporada(propuesta.getEpisodiosPorTemporada());
    nuevoElemento.setTotalUnidades(propuesta.getTotalUnidades());
    nuevoElemento.setTotalCapitulosLibro(propuesta.getTotalCapitulosLibro());
    nuevoElemento.setTotalPaginasLibro(propuesta.getTotalPaginasLibro());
    nuevoElemento.setDuracion(propuesta.getDuracion());
    nuevoElemento.setCreadoDesdePropuesta(true); // Marcar como creado desde propuesta

    // Marcar propuesta como APROBADA
    propuesta.setEstadoPropuesta(EstadoPropuesta.APROBADO);
    propuesta.setRevisor(revisor);
    // Save the review comments in the proposal
    propuesta.setComentariosRevision(dto.getComentariosRevision());

    // Use the state from the DTO or default to OFICIAL
    nuevoElemento.setEstadoContenido(
        dto.getEstadoContenido() != null ? dto.getEstadoContenido() : EstadoContenido.OFICIAL);

    // Use the publication state from the DTO or default to AVAILABLE
    nuevoElemento.setEstadoPublicacion(
        dto.getEstadoPublicacion() != null
            ? dto.getEstadoPublicacion()
            : EstadoPublicacion.AVAILABLE);

    propuestaRepo.save(propuesta);

    Elemento elementoGuardado = elementoRepo.save(nuevoElemento);
    propuesta.setElementoCreado(elementoGuardado); // Vincular el elemento creado con la propuesta
    propuestaRepo.save(propuesta);
    return new ElementoResponseDTO(elementoGuardado);
  }

  @Transactional
  public void rechazarPropuesta(Long propuestaId, Long revisorId, String motivo) {
    PropuestaElemento propuesta =
        propuestaRepo
            .findById(propuestaId)
            .orElseThrow(() -> new ResourceNotFoundException("{exception.propuesta.not_found}"));
    Usuario revisor =
        usuarioRepo
            .findById(revisorId)
            .orElseThrow(() -> new ResourceNotFoundException("{exception.user.not_found}"));
    propuesta.setEstadoPropuesta(EstadoPropuesta.RECHAZADO);
    propuesta.setRevisor(revisor);
    propuesta.setComentariosRevision(motivo);
    propuestaRepo.save(propuesta);
  }

  // --- Métodos Auxiliares ---

  private void actualizarDatosPropuesta(PropuestaElemento propuesta, PropuestaUpdateDTO dto) {
    propuesta.setTituloSugerido(dto.getTituloSugerido());
    propuesta.setDescripcionSugerida(dto.getDescripcionSugerida());
    propuesta.setTipoSugerido(dto.getTipoSugerido());
    propuesta.setGenerosSugeridos(dto.getGenerosSugeridos());
    propuesta.setUrlImagen(dto.getUrlImagen());
    propuesta.setDuracion(dto.getDuracion());
    propuesta.setEpisodiosPorTemporada(dto.getEpisodiosPorTemporada());
    propuesta.setTotalUnidades(dto.getTotalUnidades());
    propuesta.setTotalCapitulosLibro(dto.getTotalCapitulosLibro());
    propuesta.setTotalPaginasLibro(dto.getTotalPaginasLibro());
  }

  public Tipo traducirTipo(String tipoSugerido) {
    if (tipoSugerido == null || tipoSugerido.isBlank()) {
      throw new ConflictException("{exception.tipo.empty}");
    }
    return tipoRepository
        .findByNombre(tipoSugerido)
        .orElseGet(() -> tipoRepository.save(new Tipo(tipoSugerido)));
  }

  public Set<Genero> traducirGeneros(String generosSugeridosString, Tipo tipo) {
    if (generosSugeridosString == null || generosSugeridosString.isBlank()) {
      throw new ConflictException("{exception.generos.empty}");
    }

    Set<Genero> generosFinales = new HashSet<>();
    String[] generosArray = generosSugeridosString.split("\\s*,\\s*"); // Split por coma y espacios

    for (String nombreGenero : generosArray) {
      if (nombreGenero.isBlank()) continue;

      Genero genero =
          generoRepository
              .findByNombre(nombreGenero)
              .orElseGet(
                  () -> {
                    // Si el género no existe, lo creamos y lo vinculamos al tipo
                    Genero nuevo = new Genero(nombreGenero);
                    generoRepository.save(nuevo);

                    // Vincular al tipo y sincronizar SQL
                    vincularGeneroATipo(tipo, nuevo);

                    return nuevo;
                  });

      // Asegurar vinculación aunque el género ya existiera
      if (!tipo.getGenerosPermitidos().contains(genero)) {
        vincularGeneroATipo(tipo, genero);
      }

      generosFinales.add(genero);
    }

    if (generosFinales.isEmpty()) {
      throw new ConflictException("{exception.generos.invalid}");
    }
    return generosFinales;
  }

  private void vincularGeneroATipo(Tipo tipo, Genero genero) {
    tipo.getGenerosPermitidos().add(genero);
    tipoRepository.save(tipo);
    // Sincronización con archivo data.sql para persistencia entre reinicios si usas H2/memoria
    // Ojo: En producción real con BD persistente esto no es necesario, pero lo mantengo por tu
    // lógica actual.
    dataSqlSyncService.appendGenreLink(tipo.getNombre(), genero.getNombre());
  }
}
