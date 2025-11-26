package com.libratrack.api.controller;

import com.libratrack.api.dto.GeneroResponseDTO;
import com.libratrack.api.entity.Genero;
import com.libratrack.api.service.GeneroService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de acceso público (para usuarios logueados) a la lista de géneros. Utilizado para
 * filtros de búsqueda y formularios de creación.
 */
@RestController
@RequestMapping("/api/generos")
public class GeneroPublicoController {

  @Autowired private GeneroService generoService;

  /** Obtiene la lista de todos los géneros disponibles en formato DTO. */
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<GeneroResponseDTO>> getAllGenerosPublico() {
    List<Genero> generos = generoService.getAllGeneros();
    List<GeneroResponseDTO> dtos =
        generos.stream().map(GeneroResponseDTO::new).collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }
}
