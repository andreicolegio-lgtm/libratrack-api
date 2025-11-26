package com.libratrack.api.controller;

import com.libratrack.api.entity.Genero;
import com.libratrack.api.service.GeneroService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador administrativo para la gestión de géneros. Permite crear nuevos géneros manualmente.
 */
@RestController
@RequestMapping("/api/admin/generos")
@PreAuthorize("hasAuthority('ROLE_MODERADOR')")
public class GeneroController {

  @Autowired private GeneroService generoService;

  /** Lista todos los géneros disponibles en el sistema (vista de administración). */
  @GetMapping
  public ResponseEntity<List<Genero>> getAllGeneros() {
    return ResponseEntity.ok(generoService.getAllGeneros());
  }

  /** Crea un nuevo género manualmente. */
  @PostMapping
  public ResponseEntity<?> createGenero(@Valid @RequestBody Genero genero) {
    try {
      Genero nuevoGenero = generoService.createGenero(genero);
      return new ResponseEntity<>(nuevoGenero, HttpStatus.CREATED);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(e.getMessage()); // Idealmente usar un objeto de error estandarizado
    }
  }
}
