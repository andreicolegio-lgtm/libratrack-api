package com.libratrack.api.controller;

import com.libratrack.api.dto.TipoResponseDTO;
import com.libratrack.api.entity.Tipo;
import com.libratrack.api.service.TipoService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de acceso público (para usuarios logueados) a la lista de Tipos. Utilizado para
 * llenar selectores y filtros en la interfaz de usuario.
 */
@RestController
@RequestMapping("/api/tipos")
public class TipoPublicoController {

  @Autowired private TipoService tipoService;

  /** Obtiene todos los tipos disponibles junto con sus géneros permitidos. */
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<TipoResponseDTO>> getAllTiposPublico() {
    List<Tipo> tipos = tipoService.getAllTipos();
    List<TipoResponseDTO> dtos =
        tipos.stream().map(TipoResponseDTO::new).collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }
}
