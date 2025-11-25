package com.libratrack.api.service;

import com.libratrack.api.dto.ElementoFormDTO;
import com.libratrack.api.dto.ElementoRelacionDTO;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ElementoService {

  @Autowired private ElementoRepository elementoRepository;
  @Autowired private UsuarioRepository usuarioRepository;

  @Autowired private PropuestaElementoService propuestaService;

  public Page<ElementoResponseDTO> findAllElementos(
      Pageable pageable, String searchText, String tipoName, String generoName) {
    Page<Elemento> paginaDeElementos =
        elementoRepository.findElementosByFiltros(searchText, tipoName, generoName, pageable);
    return paginaDeElementos.map(ElementoResponseDTO::new);
  }

  @Transactional(readOnly = true)
  public Optional<ElementoResponseDTO> findElementoById(Long id) {

    Optional<Elemento> elementoOptional = elementoRepository.findById(id);

    if (elementoOptional.isEmpty()) {
      return Optional.empty();
    }

    Elemento elemento = elementoOptional.get();

    Hibernate.initialize(elemento.getTipo());
    Hibernate.initialize(elemento.getGeneros());
    Hibernate.initialize(elemento.getCreador());
    Hibernate.initialize(elemento.getPrecuelas());
    Hibernate.initialize(elemento.getSecuelas());

    return Optional.of(new ElementoResponseDTO(elemento));
  }

  @Transactional(readOnly = true)
  public List<ElementoRelacionDTO> findAllSimple() {
    List<Elemento> elementos = elementoRepository.findAll(Sort.by("titulo").ascending());

    return elementos.stream().map(ElementoRelacionDTO::new).collect(Collectors.toList());
  }

  @Transactional
  public ElementoResponseDTO crearElementoOficial(ElementoFormDTO dto, Long adminId) {
    Usuario admin =
        usuarioRepository
            .findById(adminId)
            .orElseThrow(() -> new ResourceNotFoundException("INVALID_USER_TOKEN"));

    Elemento nuevoElemento = new Elemento();

    Tipo tipo = propuestaService.traducirTipo(dto.getTipoNombre());
    Set<Genero> generos = propuestaService.traducirGeneros(dto.getGenerosNombres(), tipo);

    nuevoElemento.setTitulo(dto.getTitulo());
    nuevoElemento.setDescripcion(dto.getDescripcion());
    nuevoElemento.setUrlImagen(dto.getUrlImagen());
    nuevoElemento.setTipo(tipo);
    nuevoElemento.setGeneros(generos);

    nuevoElemento.setEpisodiosPorTemporada(dto.getEpisodiosPorTemporada());
    nuevoElemento.setTotalUnidades(dto.getTotalUnidades());
    nuevoElemento.setTotalCapitulosLibro(dto.getTotalCapitulosLibro());
    nuevoElemento.setTotalPaginasLibro(dto.getTotalPaginasLibro());

    nuevoElemento.setCreador(admin);
    nuevoElemento.setEstadoContenido(EstadoContenido.OFICIAL);
    nuevoElemento.setEstadoPublicacion(EstadoPublicacion.DISPONIBLE);

    Elemento elementoGuardado = elementoRepository.save(nuevoElemento);

    return new ElementoResponseDTO(elementoGuardado);
  }

  @Transactional
  public ElementoResponseDTO updateElemento(Long elementoId, ElementoFormDTO dto) {
    Elemento elemento =
        elementoRepository
            .findById(elementoId)
            .orElseThrow(() -> new ResourceNotFoundException("ELEMENT_NOT_FOUND"));

    Tipo tipo = propuestaService.traducirTipo(dto.getTipoNombre());
    Set<Genero> generos = propuestaService.traducirGeneros(dto.getGenerosNombres(), tipo);

    elemento.setTitulo(dto.getTitulo());
    elemento.setDescripcion(dto.getDescripcion());
    elemento.setUrlImagen(dto.getUrlImagen());
    elemento.setTipo(tipo);
    elemento.setGeneros(generos);

    elemento.setEpisodiosPorTemporada(dto.getEpisodiosPorTemporada());
    elemento.setTotalUnidades(dto.getTotalUnidades());
    elemento.setTotalCapitulosLibro(dto.getTotalCapitulosLibro());
    elemento.setTotalPaginasLibro(dto.getTotalPaginasLibro());

    Elemento elementoGuardado = elementoRepository.save(elemento);

    return findElementoById(elementoGuardado.getId())
        .orElseThrow(() -> new ResourceNotFoundException("ELEMENT_NOT_FOUND"));
  }

  @Transactional
  public ElementoResponseDTO oficializarElemento(Long elementoId) {
    Elemento elemento =
        elementoRepository
            .findById(elementoId)
            .orElseThrow(() -> new ResourceNotFoundException("ELEMENT_NOT_FOUND"));
    elemento.setEstadoContenido(EstadoContenido.OFICIAL);
    Elemento elementoGuardado = elementoRepository.save(elemento);
    return findElementoById(elementoGuardado.getId())
        .orElseThrow(() -> new ResourceNotFoundException("ELEMENT_NOT_FOUND"));
  }

  @Transactional
  public ElementoResponseDTO comunitarizarElemento(Long elementoId) {
    Elemento elemento =
        elementoRepository
            .findById(elementoId)
            .orElseThrow(() -> new ResourceNotFoundException("ELEMENT_NOT_FOUND"));
    elemento.setEstadoContenido(EstadoContenido.COMUNITARIO);
    Elemento elementoGuardado = elementoRepository.save(elemento);
    return findElementoById(elementoGuardado.getId())
        .orElseThrow(() -> new ResourceNotFoundException("ELEMENT_NOT_FOUND"));
  }
}
