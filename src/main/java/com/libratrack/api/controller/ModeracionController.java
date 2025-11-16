package com.libratrack.api.controller;

import com.libratrack.api.dto.ElementoResponseDTO;
import com.libratrack.api.dto.PropuestaResponseDTO;
import com.libratrack.api.dto.PropuestaUpdateDTO;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ResourceNotFoundException;
import com.libratrack.api.model.EstadoPropuesta;
import com.libratrack.api.repository.UsuarioRepository;
import com.libratrack.api.service.PropuestaElementoService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moderacion")
@PreAuthorize("hasAuthority('ROLE_MODERADOR')")
public class ModeracionController {

  @Autowired private PropuestaElementoService propuestaService;
  @Autowired private UsuarioRepository usuarioRepo;

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
      Principal principal) {

    String revisorUsername = principal.getName();
    Usuario revisor =
        usuarioRepo
            .findByUsername(revisorUsername)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Token de revisor inválido. Usuario no encontrado."));

    Long revisorId = revisor.getId();

    ElementoResponseDTO nuevoElemento =
        propuestaService.aprobarPropuesta(propuestaId, revisorId, dto);

    return ResponseEntity.ok(nuevoElemento);
  }
}
