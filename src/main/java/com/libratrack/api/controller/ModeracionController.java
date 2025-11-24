package com.libratrack.api.controller;

import com.libratrack.api.dto.ElementoResponseDTO;
import com.libratrack.api.dto.PropuestaResponseDTO;
import com.libratrack.api.dto.PropuestaUpdateDTO;
import com.libratrack.api.model.EstadoPropuesta;
import com.libratrack.api.security.CustomUserDetails;
import com.libratrack.api.service.PropuestaElementoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moderacion")
@PreAuthorize("hasAuthority('ROLE_MODERADOR')")
public class ModeracionController {

  @Autowired private PropuestaElementoService propuestaService;

  @GetMapping
  public ResponseEntity<List<PropuestaResponseDTO>> getPropuestasPorEstado(
      @RequestParam(value = "estado", defaultValue = "PENDIENTE") String estadoStr) {
    EstadoPropuesta estado;
    try {
      estado = EstadoPropuesta.valueOf(estadoStr.toUpperCase());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
    List<PropuestaResponseDTO> propuestas = propuestaService.getPropuestasPorEstado(estado);
    return ResponseEntity.ok(propuestas);
  }

  @PostMapping("/aprobar/{propuestaId}")
  public ResponseEntity<ElementoResponseDTO> aprobarPropuesta(
      @PathVariable Long propuestaId,
      @Valid @RequestBody PropuestaUpdateDTO dto,
      @AuthenticationPrincipal CustomUserDetails currentUser) {

    ElementoResponseDTO nuevoElemento =
        propuestaService.aprobarPropuesta(propuestaId, currentUser.getId(), dto);
    return ResponseEntity.ok(nuevoElemento);
  }
}
