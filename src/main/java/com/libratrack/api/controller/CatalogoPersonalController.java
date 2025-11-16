package com.libratrack.api.controller;

import com.libratrack.api.dto.CatalogoPersonalResponseDTO;
import com.libratrack.api.dto.CatalogoUpdateDTO;
import com.libratrack.api.service.CatalogoPersonalService;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalogo")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class CatalogoPersonalController {

  @Autowired private CatalogoPersonalService catalogoService;

  @GetMapping
  public ResponseEntity<List<CatalogoPersonalResponseDTO>> getMiCatalogo(Principal principal) {
    String username = principal.getName();

    List<CatalogoPersonalResponseDTO> catalogo = catalogoService.getCatalogoByUsername(username);
    return ResponseEntity.ok(catalogo);
  }

  @PostMapping("/elementos/{elementoId}")
  public ResponseEntity<CatalogoPersonalResponseDTO> addElementoAlCatalogo(
      @PathVariable Long elementoId, Principal principal) {

    CatalogoPersonalResponseDTO nuevaEntrada =
        catalogoService.addElementoAlCatalogo(principal.getName(), elementoId);
    return new ResponseEntity<>(nuevaEntrada, HttpStatus.CREATED);
  }

  @PutMapping("/elementos/{elementoId}")
  public ResponseEntity<CatalogoPersonalResponseDTO> updateElementoDelCatalogo(
      @PathVariable Long elementoId, @RequestBody CatalogoUpdateDTO dto, Principal principal) {

    CatalogoPersonalResponseDTO entradaActualizada =
        catalogoService.updateEntradaCatalogo(principal.getName(), elementoId, dto);
    return ResponseEntity.ok(entradaActualizada);
  }

  @DeleteMapping("/elementos/{elementoId}")
  public ResponseEntity<Void> removeElementoDelCatalogo(
      @PathVariable Long elementoId, Principal principal) {

    catalogoService.removeElementoDelCatalogo(principal.getName(), elementoId);
    return ResponseEntity.noContent().build();
  }
}
