package com.libratrack.api.controller;

import com.libratrack.api.dto.CatalogoPersonalResponseDTO;
import com.libratrack.api.dto.CatalogoUpdateDTO;
import com.libratrack.api.security.CustomUserDetails;
import com.libratrack.api.service.CatalogoPersonalService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestionar la biblioteca personal del usuario autenticado. Permite listar,
 * añadir, actualizar y eliminar elementos de su colección.
 */
@RestController
@RequestMapping("/api/catalogo")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class CatalogoPersonalController {

  @Autowired private CatalogoPersonalService catalogoService;

  /** Obtiene todos los elementos guardados en la biblioteca del usuario actual. */
  @GetMapping
  public ResponseEntity<List<CatalogoPersonalResponseDTO>> getMiCatalogo(
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    List<CatalogoPersonalResponseDTO> catalogo =
        catalogoService.getCatalogoByUserId(currentUser.getId());
    return ResponseEntity.ok(catalogo);
  }

  /**
   * Añade un elemento existente al catálogo personal del usuario. El estado inicial será PENDIENTE
   * por defecto.
   */
  @PostMapping("/elementos/{elementoId}")
  public ResponseEntity<CatalogoPersonalResponseDTO> addElementoAlCatalogo(
      @PathVariable Long elementoId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    CatalogoPersonalResponseDTO nuevaEntrada =
        catalogoService.addElementoAlCatalogo(currentUser.getId(), elementoId);
    return new ResponseEntity<>(nuevaEntrada, HttpStatus.CREATED);
  }

  /** Actualiza el estado (ej. VIENDO, TERMINADO) o el progreso (capítulo/página) de una entrada. */
  @PutMapping("/elementos/{elementoId}")
  public ResponseEntity<CatalogoPersonalResponseDTO> updateElementoDelCatalogo(
      @PathVariable Long elementoId,
      @RequestBody CatalogoUpdateDTO dto,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    CatalogoPersonalResponseDTO entradaActualizada =
        catalogoService.updateEntradaCatalogo(currentUser.getId(), elementoId, dto);
    return ResponseEntity.ok(entradaActualizada);
  }

  /**
   * Elimina un elemento del catálogo personal. Devuelve 204 No Content si la operación es exitosa.
   */
  @DeleteMapping("/elementos/{elementoId}")
  public ResponseEntity<Void> removeElementoDelCatalogo(
      @PathVariable Long elementoId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    catalogoService.removeElementoDelCatalogo(currentUser.getId(), elementoId);
    return ResponseEntity.noContent().build();
  }

  /**
   * Marca o desmarca un elemento como "Favorito". Si el elemento no estaba en el catálogo, se añade
   * automáticamente.
   */
  @PutMapping("/favorito/{id}")
  public ResponseEntity<Void> toggleFavorito(
      @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails currentUser) {
    catalogoService.toggleFavorito(currentUser.getId(), id);
    return ResponseEntity.noContent().build();
  }
}
