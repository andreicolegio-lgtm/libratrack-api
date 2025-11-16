package com.libratrack.api.controller;

import com.libratrack.api.entity.Tipo;
import com.libratrack.api.service.TipoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/tipos")
@PreAuthorize("hasAuthority('ROLE_MODERADOR')")
public class TipoController {

  @Autowired private TipoService tipoService;

  @GetMapping
  public ResponseEntity<List<Tipo>> getAllTipos() {
    return ResponseEntity.ok(tipoService.getAllTipos());
  }

  @PostMapping
  public ResponseEntity<?> createTipo(@Valid @RequestBody Tipo tipo) {
    try {
      Tipo nuevoTipo = tipoService.createTipo(tipo);
      return new ResponseEntity<>(nuevoTipo, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }
}
