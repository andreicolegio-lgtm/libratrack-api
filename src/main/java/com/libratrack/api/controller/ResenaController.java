package com.libratrack.api.controller;

import com.libratrack.api.dto.ResenaDTO;
import com.libratrack.api.dto.ResenaResponseDTO;
import com.libratrack.api.security.CustomUserDetails;
import com.libratrack.api.service.ResenaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Controlador para gestionar las reseñas y valoraciones de los usuarios. */
@RestController
@RequestMapping("/api/resenas")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class ResenaController {

  @Autowired private ResenaService resenaService;

  /** Obtiene todas las reseñas de un elemento específico. */
  @GetMapping("/elemento/{elementoId}")
  public ResponseEntity<List<ResenaResponseDTO>> getResenasDelElemento(
      @PathVariable Long elementoId) {
    List<ResenaResponseDTO> resenas = resenaService.getResenasByElementoId(elementoId);
    return ResponseEntity.ok(resenas);
  }

  /**
   * Crea una nueva reseña para un elemento. El usuario solo puede crear una reseña por elemento.
   */
  @PostMapping
  public ResponseEntity<ResenaResponseDTO> createResena(
      @Valid @RequestBody ResenaDTO resenaDTO,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    ResenaResponseDTO nuevaResena = resenaService.createResena(resenaDTO, currentUser.getId());
    return new ResponseEntity<>(nuevaResena, HttpStatus.CREATED);
  }

  /**
   * Actualiza una reseña existente.
   *
   * @param resenaId ID de la reseña a actualizar
   * @param resenaDTO Datos nuevos de la reseña
   * @param currentUser Usuario autenticado
   * @return ResponseEntity con los datos actualizados
   */
  @PutMapping("/{resenaId}")
  public ResponseEntity<ResenaResponseDTO> updateResena(
      @PathVariable Long resenaId,
      @Valid @RequestBody ResenaDTO resenaDTO,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    ResenaResponseDTO resenaActualizada =
        resenaService.updateResena(resenaId, resenaDTO, currentUser.getId());
    return ResponseEntity.ok(resenaActualizada);
  }

  /**
   * Elimina una reseña existente.
   *
   * @param resenaId ID de la reseña a eliminar
   * @param currentUser Usuario autenticado
   * @return ResponseEntity sin contenido
   */
  @DeleteMapping("/{resenaId}")
  public ResponseEntity<Void> deleteResena(
      @PathVariable Long resenaId,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    resenaService.deleteResena(resenaId, currentUser);
    return ResponseEntity.noContent().build();
  }
}
