package com.libratrack.api.controller;

import com.libratrack.api.dto.ElementoResponseDTO;
import com.libratrack.api.dto.PaginatedResponse;
import com.libratrack.api.dto.PropuestaResponseDTO;
import com.libratrack.api.dto.PropuestaUpdateDTO;
import com.libratrack.api.model.EstadoPropuesta;
import com.libratrack.api.security.CustomUserDetails;
import com.libratrack.api.service.AdminService;
import com.libratrack.api.service.PropuestaElementoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
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

  @Autowired private AdminService adminService;

  /**
   * Lista las propuestas filtradas por estado, texto, tipos y géneros.
   */
  @GetMapping
  public ResponseEntity<List<PropuestaResponseDTO>> getPropuestasPorEstado(
      @RequestParam(value = "estado", defaultValue = "PENDIENTE") String estadoStr,
      @RequestParam(value = "search", required = false) String search,
      @RequestParam(value = "types", required = false) List<String> types,
      @RequestParam(value = "genres", required = false) List<String> genres,
      @RequestParam(value = "sort", defaultValue = "DATE") String sort,
      @RequestParam(value = "asc", defaultValue = "false") boolean asc) {

    EstadoPropuesta estado;
    try {
      estado = EstadoPropuesta.valueOf(estadoStr.toUpperCase());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }

    List<PropuestaResponseDTO> propuestas =
        propuestaService.getPropuestasPorEstado(estado, search, types, genres, sort, asc);
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

  /**
   * Rechaza una propuesta, cambiando su estado a RECHAZADO. Se requiere el ID de la propuesta a
   * rechazar.
   */
  @PostMapping("/rechazar/{propuestaId}")
  public ResponseEntity<Void> rechazarPropuesta(
      @PathVariable Long propuestaId,
      @RequestBody Map<String, String> body,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    String motivo = body.get("motivo");
    propuestaService.rechazarPropuesta(propuestaId, currentUser.getId(), motivo);
    return ResponseEntity.ok().build();
  }

  /**
   * Fetches elements created by the authenticated admin.
   *
   * @param page The page number (default is 0).
   * @param size The page size (default is 10).
   * @param search Optional search term to filter by title.
   * @param types Optional list of types to filter the elements.
   * @param genres Optional list of genres to filter the elements.
   * @param sort The sorting criteria (default is by title).
   * @param asc  Whether the sorting should be ascending (default is true).
   * @return A paginated response of elements created by the admin.
   */
  @GetMapping("/elementos-creados")
  public ResponseEntity<PaginatedResponse<ElementoResponseDTO>> getElementosCreados(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) List<String> types,
      @RequestParam(required = false) List<String> genres,
      @RequestParam(defaultValue = "DATE") String sort,
      @RequestParam(defaultValue = "false") boolean asc,
      @AuthenticationPrincipal CustomUserDetails currentUser) {

    return ResponseEntity.ok(
        adminService.getElementosCreados(
            currentUser.getId(), page, size, search, types, genres, sort, asc));
  }
}
