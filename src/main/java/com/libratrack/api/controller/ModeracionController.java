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

/**
 * Controlador para el panel de moderación. Permite revisar, listar y aprobar propuestas de
 * contenido enviadas por la comunidad.
 */
@RestController
@RequestMapping("/api/moderacion")
@PreAuthorize("hasAuthority('ROLE_MODERADOR')")
public class ModeracionController {

  @Autowired private PropuestaElementoService propuestaService;

  /**
   * Lista las propuestas filtradas por su estado (PENDIENTE, APROBADO, RECHAZADO). Por defecto
   * muestra las PENDIENTES.
   */
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

  /**
   * Aprueba una propuesta y la convierte en un Elemento oficial o comunitario. Permite editar los
   * datos finales antes de la aprobación (ej. corregir typos en el título).
   */
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
