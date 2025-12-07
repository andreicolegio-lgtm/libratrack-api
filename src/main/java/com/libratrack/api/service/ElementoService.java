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
import org.springframework.data.domain.PageRequest;
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
    nuevoElemento.setEstadoPublicacion(EstadoPublicacion.AVAILABLE);
    nuevoElemento.setCreadoDesdePropuesta(false); // Aseguramos que no venga de propuesta

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
    // Mapeo básico (Existente)
    Tipo tipo = propuestaService.traducirTipo(dto.getTipoNombre());
    Set<Genero> generos = propuestaService.traducirGeneros(dto.getGenerosNombres(), tipo);
    elemento.setTitulo(dto.getTitulo());
    elemento.setDescripcion(dto.getDescripcion());
    elemento.setUrlImagen(dto.getUrlImagen());
    elemento.setTipo(tipo);
    elemento.setGeneros(generos);

    // Detalles técnicos (Existente)
    elemento.setEpisodiosPorTemporada(dto.getEpisodiosPorTemporada());
    elemento.setTotalUnidades(dto.getTotalUnidades());
    elemento.setTotalCapitulosLibro(dto.getTotalCapitulosLibro());
    elemento.setTotalPaginasLibro(dto.getTotalPaginasLibro());
    elemento.setDuracion(dto.getDuracion());

    // Estado de Publicación
    // Si viene nulo, mantenemos el que tenía o ponemos uno por defecto
    if (dto.getEstadoPublicacion() != null) {
        elemento.setEstadoPublicacion(dto.getEstadoPublicacion());
    } else if (elemento.getEstadoPublicacion() == null) {
        elemento.setEstadoPublicacion(EstadoPublicacion.AVAILABLE); // Valor por defecto
    }

    // Secuelas / Cronología
    if (dto.getSecuelaIds() != null && !dto.getSecuelaIds().isEmpty()) {
        Set<Elemento> nuevasSecuelas = dto.getSecuelaIds().stream()
            .map(id -> elementoRepository.findById(id).orElse(null))
            .filter(java.util.Objects::nonNull)
            .collect(java.util.stream.Collectors.toSet());

        elemento.setSecuelas(nuevasSecuelas);
    } else if (dto.getSecuelaIds() != null) {
        // Si envían una lista vacía explícitamente, limpiamos las relaciones
        elemento.getSecuelas().clear();
    }
  }

  @Transactional(readOnly = true)
  public Page<ElementoResponseDTO> searchElementos(
      Pageable pageable,
      String searchText,
      List<String> types,
      List<String> genres,
      String sortMode,
      boolean isAscending) {

    // 1. TRADUCCIÓN DE ORDENAMIENTO
    String sortColumn = "titulo"; // Por defecto ALPHA
    if ("DATE".equalsIgnoreCase(sortMode)) {
        sortColumn = "id"; // O "id" si prefieres orden de creación
    }

    // 2. Construcción del Sort con la columna real
    Sort sort = Sort.by(isAscending ? Sort.Direction.ASC : Sort.Direction.DESC, sortColumn);
    
    // 3. Crear PageRequest
    Pageable sortedPageable = PageRequest.of(
        pageable.getPageNumber(), 
        pageable.getPageSize(), 
        sort
    );

    // Normalización de filtros
    List<String> typesFilter = (types != null && !types.isEmpty()) ? types : null;
    List<String> genresFilter = (genres != null && !genres.isEmpty()) ? genres : null;

    // Llamada al repositorio (Asegúrate de haber aplicado el paso 3 abajo)
    Page<Elemento> paginaDeElementos =
        elementoRepository.searchPublicElementos(searchText, typesFilter, genresFilter, sortedPageable);

    return paginaDeElementos.map(ElementoResponseDTO::new);
  }
}
