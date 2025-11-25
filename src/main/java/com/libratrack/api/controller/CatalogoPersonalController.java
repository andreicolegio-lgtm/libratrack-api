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

@RestController
@RequestMapping("/api/catalogo")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class CatalogoPersonalController {

  @Autowired private CatalogoPersonalService catalogoService;

  @GetMapping
  public ResponseEntity<List<CatalogoPersonalResponseDTO>> getMiCatalogo(
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    List<CatalogoPersonalResponseDTO> catalogo =
        catalogoService.getCatalogoByUserId(currentUser.getId());
    return ResponseEntity.ok(catalogo);
  }

  @PostMapping("/elementos/{elementoId}")
  public ResponseEntity<CatalogoPersonalResponseDTO> addElementoAlCatalogo(
      @PathVariable Long elementoId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    CatalogoPersonalResponseDTO nuevaEntrada =
        catalogoService.addElementoAlCatalogo(currentUser.getId(), elementoId);
    return new ResponseEntity<>(nuevaEntrada, HttpStatus.CREATED);
  }

  @PutMapping("/elementos/{elementoId}")
  public ResponseEntity<CatalogoPersonalResponseDTO> updateElementoDelCatalogo(
      @PathVariable Long elementoId,
      @RequestBody CatalogoUpdateDTO dto,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    CatalogoPersonalResponseDTO entradaActualizada =
        catalogoService.updateEntradaCatalogo(currentUser.getId(), elementoId, dto);
    return ResponseEntity.ok(entradaActualizada);
  }

  @DeleteMapping("/elementos/{elementoId}")
  public ResponseEntity<Void> removeElementoDelCatalogo(
      @PathVariable Long elementoId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    catalogoService.removeElementoDelCatalogo(currentUser.getId(), elementoId);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/favorito/{id}")
  public ResponseEntity<Void> toggleFavorito(
      @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails currentUser) {
    catalogoService.toggleFavorito(currentUser.getId(), id);
    return ResponseEntity.noContent().build();
  }
}
