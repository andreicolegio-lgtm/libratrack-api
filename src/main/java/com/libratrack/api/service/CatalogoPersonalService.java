package com.libratrack.api.service;

import com.libratrack.api.dto.CatalogoPersonalResponseDTO;
import com.libratrack.api.dto.CatalogoUpdateDTO;
import com.libratrack.api.entity.CatalogoPersonal;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ConflictException;
import com.libratrack.api.exception.ResourceNotFoundException;
import com.libratrack.api.model.EstadoPersonal;
import com.libratrack.api.repository.CatalogoPersonalRepository;
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.UsuarioRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para la gestión de la biblioteca personal del usuario. Permite añadir elementos,
 * actualizar el progreso de lectura/visualización y gestionar favoritos.
 */
@Service
public class CatalogoPersonalService {

  @Autowired private CatalogoPersonalRepository catalogoRepo;
  @Autowired private UsuarioRepository usuarioRepo;
  @Autowired private ElementoRepository elementoRepo;

  /** Obtiene la lista completa de elementos en la biblioteca del usuario. */
  @Transactional(readOnly = true)
  public List<CatalogoPersonalResponseDTO> getCatalogoByUserId(Long userId) {
    // Utilizamos el método optimizado del repositorio que hace JOIN FETCH
    List<CatalogoPersonal> catalogo = catalogoRepo.findByUsuarioId(userId);
    return catalogo.stream().map(CatalogoPersonalResponseDTO::new).collect(Collectors.toList());
  }

  /** Añade un nuevo elemento al catálogo personal con estado inicial PENDIENTE. */
  @Transactional
  public CatalogoPersonalResponseDTO addElementoAlCatalogo(Long userId, Long elementoId) {
    Usuario usuario =
        usuarioRepo
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("{exception.user.not_found}"));

    Elemento elemento =
        elementoRepo
            .findById(elementoId)
            .orElseThrow(() -> new ResourceNotFoundException("{exception.elemento.not_found}"));

    // Verificar si ya existe para evitar duplicados
    if (catalogoRepo.findByUsuarioIdAndElementoId(usuario.getId(), elemento.getId()).isPresent()) {
      throw new ConflictException("{exception.catalogo.already_exists}");
    }

    CatalogoPersonal nuevaEntrada = new CatalogoPersonal();
    nuevaEntrada.setUsuario(usuario);
    nuevaEntrada.setElemento(elemento);
    nuevaEntrada.setEstadoPersonal(EstadoPersonal.PENDIENTE);
    // Los valores de progreso se inicializan por defecto en la entidad (0 o 1)

    CatalogoPersonal entradaGuardada = catalogoRepo.save(nuevaEntrada);
    return new CatalogoPersonalResponseDTO(entradaGuardada);
  }

  /** Actualiza el estado o el progreso de un elemento en el catálogo. */
  @Transactional
  public CatalogoPersonalResponseDTO updateEntradaCatalogo(
      Long userId, Long elementoId, CatalogoUpdateDTO dto) {

    CatalogoPersonal entrada =
        catalogoRepo
            .findByUsuarioIdAndElementoId(userId, elementoId)
            .orElseThrow(() -> new ResourceNotFoundException("{exception.catalogo.not_found}"));

    if (dto.getEstadoPersonal() != null) {
      entrada.setEstadoPersonal(dto.getEstadoPersonal());
    }

    // Actualización de progreso con validaciones básicas (la entidad ya tiene @Min)
    if (dto.getTemporadaActual() != null) entrada.setTemporadaActual(dto.getTemporadaActual());
    if (dto.getUnidadActual() != null) entrada.setUnidadActual(dto.getUnidadActual());
    if (dto.getCapituloActual() != null) entrada.setCapituloActual(dto.getCapituloActual());
    if (dto.getPaginaActual() != null) entrada.setPaginaActual(dto.getPaginaActual());

    CatalogoPersonal entradaGuardada = catalogoRepo.save(entrada);
    return new CatalogoPersonalResponseDTO(entradaGuardada);
  }

  /** Elimina un elemento del catálogo personal. */
  @Transactional
  public void removeElementoDelCatalogo(Long userId, Long elementoId) {
    CatalogoPersonal entrada =
        catalogoRepo
            .findByUsuarioIdAndElementoId(userId, elementoId)
            .orElseThrow(() -> new ResourceNotFoundException("{exception.catalogo.not_found}"));

    catalogoRepo.delete(entrada);
  }

  /**
   * Alterna el estado de "Favorito" de un elemento. Si el elemento no está en el catálogo, se añade
   * automáticamente como PENDIENTE y Favorito.
   */
  @Transactional
  public void toggleFavorito(Long userId, Long elementoId) {
    catalogoRepo
        .findByUsuarioIdAndElementoIdWithFetch(userId, elementoId)
        .ifPresentOrElse(
            entrada -> {
              entrada.setEsFavorito(!entrada.getEsFavorito());
              catalogoRepo.save(entrada);
            },
            () -> {
              // Si no existe, lo creamos al vuelo
              Usuario usuario =
                  usuarioRepo
                      .findById(userId)
                      .orElseThrow(
                          () -> new ResourceNotFoundException("{exception.user.not_found}"));
              Elemento elemento =
                  elementoRepo
                      .findById(elementoId)
                      .orElseThrow(
                          () -> new ResourceNotFoundException("{exception.elemento.not_found}"));

              CatalogoPersonal nuevaEntrada = new CatalogoPersonal();
              nuevaEntrada.setUsuario(usuario);
              nuevaEntrada.setElemento(elemento);
              nuevaEntrada.setEstadoPersonal(EstadoPersonal.PENDIENTE);
              nuevaEntrada.setEsFavorito(true);

              catalogoRepo.save(nuevaEntrada);
            });
  }
}
