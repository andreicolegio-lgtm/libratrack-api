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

@RestController
@RequestMapping("/api/admin/generos")
@PreAuthorize("hasAuthority('ROLE_MODERADOR')")
public class GeneroController {

  @Autowired private GeneroService generoService;

  @GetMapping
  public ResponseEntity<List<Genero>> getAllGeneros() {
    return ResponseEntity.ok(generoService.getAllGeneros());
  }

  @PostMapping
  public ResponseEntity<?> createGenero(@Valid @RequestBody Genero genero) {
    try {
      Genero nuevoGenero = generoService.createGenero(genero);
      return new ResponseEntity<>(nuevoGenero, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }
}
