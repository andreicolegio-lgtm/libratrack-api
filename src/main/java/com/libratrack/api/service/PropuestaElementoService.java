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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropuestaElementoService {

  @Autowired private PropuestaElementoRepository propuestaRepo;
  @Autowired private UsuarioRepository usuarioRepo;
  @Autowired private ElementoRepository elementoRepo;
  @Autowired private TipoRepository tipoRepository;
  @Autowired private GeneroRepository generoRepository;

  @Transactional
  public PropuestaResponseDTO createPropuesta(PropuestaRequestDTO dto, String proponenteUsername) {
    Usuario proponente =
        usuarioRepo
            .findByUsername(proponenteUsername)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario proponente no encontrado."));

    PropuestaElemento nuevaPropuesta = new PropuestaElemento();
    nuevaPropuesta.setProponente(proponente);
    nuevaPropuesta.setTituloSugerido(dto.getTituloSugerido());
    nuevaPropuesta.setDescripcionSugerida(dto.getDescripcionSugerida());
    nuevaPropuesta.setTipoSugerido(dto.getTipoSugerido());
    nuevaPropuesta.setGenerosSugeridos(dto.getGenerosSugeridos());

    nuevaPropuesta.setEpisodiosPorTemporada(dto.getEpisodiosPorTemporada());
    nuevaPropuesta.setTotalUnidades(dto.getTotalUnidades());
    nuevaPropuesta.setTotalCapitulosLibro(dto.getTotalCapitulosLibro());
    nuevaPropuesta.setTotalPaginasLibro(dto.getTotalPaginasLibro());

    PropuestaElemento propuestaGuardada = propuestaRepo.save(nuevaPropuesta);

    return new PropuestaResponseDTO(propuestaGuardada);
  }

  @Transactional(readOnly = true)
  public List<PropuestaResponseDTO> getPropuestasPorEstado(EstadoPropuesta estado) {
    List<PropuestaElemento> propuestas = propuestaRepo.findByEstadoPropuesta(estado);
    return propuestas.stream().map(PropuestaResponseDTO::new).collect(Collectors.toList());
  }

  @Transactional
  public ElementoResponseDTO aprobarPropuesta(
      Long propuestaId, Long revisorId, PropuestaUpdateDTO dto) {

    Usuario revisor =
        usuarioRepo
            .findById(revisorId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario revisor no encontrado."));
    PropuestaElemento propuesta =
        propuestaRepo
            .findById(propuestaId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Propuesta no encontrada con id: " + propuestaId));
    if (propuesta.getEstadoPropuesta() != EstadoPropuesta.PENDIENTE) {
      throw new ConflictException("PROPOSAL_ALREADY_HANDLED");
    }

    updatePropuestaFields(propuesta, dto);

    Tipo tipoFinal = traducirTipo(propuesta.getTipoSugerido());
    Set<Genero> generosFinales = traducirGeneros(propuesta.getGenerosSugeridos());

    Elemento nuevoElemento = new Elemento();
    nuevoElemento.setTitulo(propuesta.getTituloSugerido());
    nuevoElemento.setDescripcion(propuesta.getDescripcionSugerida());
    nuevoElemento.setCreador(propuesta.getProponente());
    nuevoElemento.setTipo(tipoFinal);
    nuevoElemento.setGeneros(generosFinales);
    nuevoElemento.setEstadoContenido(EstadoContenido.COMUNITARIO);
    nuevoElemento.setEstadoPublicacion(EstadoPublicacion.DISPONIBLE);
    nuevoElemento.setUrlImagen(propuesta.getUrlImagen());

    nuevoElemento.setEpisodiosPorTemporada(propuesta.getEpisodiosPorTemporada());
    nuevoElemento.setTotalUnidades(propuesta.getTotalUnidades());
    nuevoElemento.setTotalCapitulosLibro(propuesta.getTotalCapitulosLibro());
    nuevoElemento.setTotalPaginasLibro(propuesta.getTotalPaginasLibro());

    propuesta.setEstadoPropuesta(EstadoPropuesta.APROBADO);
    propuesta.setRevisor(revisor);
    propuestaRepo.save(propuesta);

    Elemento elementoGuardado = elementoRepo.save(nuevoElemento);

    return new ElementoResponseDTO(elementoGuardado);
  }

  private void updatePropuestaFields(PropuestaElemento propuesta, PropuestaUpdateDTO dto) {
    propuesta.setTituloSugerido(dto.getTituloSugerido());
    propuesta.setDescripcionSugerida(dto.getDescripcionSugerida());
    propuesta.setTipoSugerido(dto.getTipoSugerido());
    propuesta.setGenerosSugeridos(dto.getGenerosSugeridos());
    propuesta.setUrlImagen(dto.getUrlImagen());

    propuesta.setEpisodiosPorTemporada(dto.getEpisodiosPorTemporada());
    propuesta.setTotalUnidades(dto.getTotalUnidades());
    propuesta.setTotalCapitulosLibro(dto.getTotalCapitulosLibro());
    propuesta.setTotalPaginasLibro(dto.getTotalPaginasLibro());
  }

  public Tipo traducirTipo(String tipoSugerido) {
    if (tipoSugerido == null || tipoSugerido.isBlank()) {
      throw new ConflictException("TYPE_EMPTY");
    }
    return tipoRepository
        .findByNombre(tipoSugerido)
        .orElseGet(() -> tipoRepository.save(new Tipo(tipoSugerido)));
  }

  public Set<Genero> traducirGeneros(String generosSugeridosString) {
    if (generosSugeridosString == null || generosSugeridosString.isBlank()) {
      throw new ConflictException("GENRES_EMPTY");
    }
    Set<Genero> generosFinales = new HashSet<>();
    String[] generosSugeridosArray = generosSugeridosString.split("\\s*,\\s*");
    for (String nombreGenero : generosSugeridosArray) {
      if (nombreGenero.isBlank()) continue;
      Genero genero =
          generoRepository
              .findByNombre(nombreGenero)
              .orElseGet(() -> generoRepository.save(new Genero(nombreGenero)));
      generosFinales.add(genero);
    }
    if (generosFinales.isEmpty()) {
      throw new ConflictException("GENRE_INVALID");
    }
    return generosFinales;
  }
}
