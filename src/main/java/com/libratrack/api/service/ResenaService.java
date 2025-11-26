package com.libratrack.api.service;

import com.libratrack.api.dto.ResenaDTO;
import com.libratrack.api.dto.ResenaResponseDTO;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Resena;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ConflictException;
import com.libratrack.api.exception.ResourceNotFoundException;
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.ResenaRepository;
import com.libratrack.api.repository.UsuarioRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Servicio para la gestión de reseñas y valoraciones de contenido. */
@Service
public class ResenaService {

  @Autowired private ResenaRepository resenaRepo;
  @Autowired private UsuarioRepository usuarioRepo;
  @Autowired private ElementoRepository elementoRepo;

  /** Obtiene todas las reseñas asociadas a un elemento, ordenadas por fecha. */
  @Transactional(readOnly = true)
  public List<ResenaResponseDTO> getResenasByElementoId(Long elementoId) {
    if (!elementoRepo.existsById(elementoId)) {
      throw new ResourceNotFoundException("{exception.elemento.not_found}");
    }

    return resenaRepo.findByElementoIdOrderByFechaCreacionDesc(elementoId).stream()
        .map(ResenaResponseDTO::new)
        .collect(Collectors.toList());
  }

  /**
   * Crea una nueva reseña para un elemento. Valida que el usuario no haya reseñado previamente el
   * mismo contenido.
   */
  @Transactional
  public ResenaResponseDTO createResena(ResenaDTO dto, Long userId) {
    // 1. Obtener entidades
    Usuario usuario =
        usuarioRepo
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("{exception.user.not_found}"));

    Elemento elemento =
        elementoRepo
            .findById(dto.getElementoId())
            .orElseThrow(() -> new ResourceNotFoundException("{exception.elemento.not_found}"));

    // 2. Validar duplicados
    if (resenaRepo.findByUsuarioIdAndElementoId(usuario.getId(), elemento.getId()).isPresent()) {
      throw new ConflictException("{exception.resena.already_exists}");
    }

    // 3. Validar rango de valoración
    if (dto.getValoracion() == null || dto.getValoracion() < 1 || dto.getValoracion() > 5) {
      throw new IllegalArgumentException("{validation.resena.valoracion.range}");
    }

    // 4. Crear y guardar
    Resena nuevaResena = new Resena();
    nuevaResena.setUsuario(usuario);
    nuevaResena.setElemento(elemento);
    nuevaResena.setValoracion(dto.getValoracion());
    nuevaResena.setTextoResena(dto.getTextoResena());

    Resena resenaGuardada = resenaRepo.save(nuevaResena);

    return new ResenaResponseDTO(resenaGuardada);
  }
}
