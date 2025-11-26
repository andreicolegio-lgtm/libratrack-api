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

/**
 * Servicio que encapsula la lógica de negocio relacionada con los Elementos (Libros, Series, etc.).
 * Maneja la creación, búsqueda, actualización y cambio de estado de los contenidos.
 */
@Service
public class ElementoService {

  @Autowired private ElementoRepository elementoRepository;
  @Autowired private UsuarioRepository usuarioRepository;
  @Autowired private PropuestaElementoService propuestaService;

  /** Recupera una página de elementos aplicando filtros opcionales. */
  @Transactional(readOnly = true)
  public Page<ElementoResponseDTO> findAllElementos(
      Pageable pageable, String searchText, List<String> types, List<String> genres) {

    // Normalización de filtros para evitar enviar listas vacías al repositorio
    List<String> typesFilter = (types != null && !types.isEmpty()) ? types : null;
    List<String> genresFilter = (genres != null && !genres.isEmpty()) ? genres : null;

    Page<Elemento> paginaDeElementos =
        elementoRepository.findElementosByFiltros(searchText, typesFilter, genresFilter, pageable);

    return paginaDeElementos.map(ElementoResponseDTO::new);
  }

  /**
   * Busca un elemento por su ID e inicializa sus relaciones Lazy para evitar
   * LazyInitializationException.
   */
  @Transactional(readOnly = true)
  public Optional<ElementoResponseDTO> findElementoById(Long id) {
    return elementoRepository
        .findById(id)
        .map(
            elemento -> {
              // Inicialización forzada de proxies Hibernate para serialización completa
              Hibernate.initialize(elemento.getTipo());
              Hibernate.initialize(elemento.getGeneros());
              Hibernate.initialize(elemento.getCreador());
              Hibernate.initialize(elemento.getPrecuelas());
              Hibernate.initialize(elemento.getSecuelas());
              return new ElementoResponseDTO(elemento);
            });
  }

  /**
   * Recupera una lista simplificada de todos los elementos, ideal para selectores o autocompletado.
   */
  @Transactional(readOnly = true)
  public List<ElementoRelacionDTO> findAllSimple() {
    return elementoRepository.findAll(Sort.by("titulo").ascending()).stream()
        .map(ElementoRelacionDTO::new)
        .collect(Collectors.toList());
  }

  /** Crea un nuevo elemento marcado directamente como OFICIAL (solo para Admins). */
  @Transactional
  public ElementoResponseDTO crearElementoOficial(ElementoFormDTO dto, Long adminId) {
    Usuario admin =
        usuarioRepository
            .findById(adminId)
            .orElseThrow(() -> new ResourceNotFoundException("{exception.user.not_found}"));

    Elemento nuevoElemento = new Elemento();
    mapDtoToEntity(dto, nuevoElemento);

    nuevoElemento.setCreador(admin);
    nuevoElemento.setEstadoContenido(EstadoContenido.OFICIAL);
    nuevoElemento.setEstadoPublicacion(EstadoPublicacion.DISPONIBLE);

    Elemento elementoGuardado = elementoRepository.save(nuevoElemento);
    return new ElementoResponseDTO(elementoGuardado);
  }

  /** Actualiza los datos de un elemento existente. */
  @Transactional
  public ElementoResponseDTO updateElemento(Long elementoId, ElementoFormDTO dto) {
    Elemento elemento =
        elementoRepository
            .findById(elementoId)
            .orElseThrow(() -> new ResourceNotFoundException("{exception.elemento.not_found}"));

    mapDtoToEntity(dto, elemento);

    Elemento elementoGuardado = elementoRepository.save(elemento);

    // Devolvemos el DTO completo re-consultando para asegurar que las relaciones estén cargadas
    return findElementoById(elementoGuardado.getId())
        .orElseThrow(() -> new ResourceNotFoundException("{exception.elemento.not_found}"));
  }

  /** Cambia el estado de un elemento a OFICIAL. */
  @Transactional
  public ElementoResponseDTO oficializarElemento(Long elementoId) {
    return cambiarEstadoContenido(elementoId, EstadoContenido.OFICIAL);
  }

  /** Cambia el estado de un elemento a COMUNITARIO. */
  @Transactional
  public ElementoResponseDTO comunitarizarElemento(Long elementoId) {
    return cambiarEstadoContenido(elementoId, EstadoContenido.COMUNITARIO);
  }

  // =============================================================================================
  // MÉTODOS AUXILIARES PRIVADOS
  // =============================================================================================

  private ElementoResponseDTO cambiarEstadoContenido(Long elementoId, EstadoContenido nuevoEstado) {
    Elemento elemento =
        elementoRepository
            .findById(elementoId)
            .orElseThrow(() -> new ResourceNotFoundException("{exception.elemento.not_found}"));

    elemento.setEstadoContenido(nuevoEstado);
    elementoRepository.save(elemento);

    return findElementoById(elemento.getId())
        .orElseThrow(() -> new ResourceNotFoundException("{exception.elemento.not_found}"));
  }

  private void mapDtoToEntity(ElementoFormDTO dto, Elemento elemento) {
    // Lógica de traducción delegada al servicio de propuestas para reutilización
    Tipo tipo = propuestaService.traducirTipo(dto.getTipoNombre());
    Set<Genero> generos = propuestaService.traducirGeneros(dto.getGenerosNombres(), tipo);

    elemento.setTitulo(dto.getTitulo());
    elemento.setDescripcion(dto.getDescripcion());
    elemento.setUrlImagen(dto.getUrlImagen());
    elemento.setTipo(tipo);
    elemento.setGeneros(generos);

    // Detalles técnicos
    elemento.setEpisodiosPorTemporada(dto.getEpisodiosPorTemporada());
    elemento.setTotalUnidades(dto.getTotalUnidades());
    elemento.setTotalCapitulosLibro(dto.getTotalCapitulosLibro());
    elemento.setTotalPaginasLibro(dto.getTotalPaginasLibro());
    elemento.setDuracion(dto.getDuracion());

    // Nota: La gestión de secuelas/precuelas se podría añadir aquí si el DTO trae IDs
  }
}
