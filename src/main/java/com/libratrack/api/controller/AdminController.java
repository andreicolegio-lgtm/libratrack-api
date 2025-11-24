package com.libratrack.api.controller;

import com.libratrack.api.dto.ElementoFormDTO;
import com.libratrack.api.dto.ElementoResponseDTO;
import com.libratrack.api.dto.RolUpdateDTO;
import com.libratrack.api.dto.UsuarioResponseDTO;
import com.libratrack.api.security.CustomUserDetails;
import com.libratrack.api.service.ElementoService;
import com.libratrack.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

  @Autowired private UsuarioService usuarioService;

  @Autowired private ElementoService elementoService;

  @GetMapping("/usuarios")
  public ResponseEntity<Page<UsuarioResponseDTO>> getAllUsuarios(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size,
      @RequestParam(value = "search", required = false) String search,
      @RequestParam(value = "role", required = false) String roleFilter) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());

    Page<UsuarioResponseDTO> usuarios = usuarioService.getAllUsuarios(pageable, search, roleFilter);

    return ResponseEntity.ok(usuarios);
  }

  @PutMapping("/usuarios/{id}/roles")
  public ResponseEntity<UsuarioResponseDTO> updateUserRoles(
      @PathVariable Long id, @Valid @RequestBody RolUpdateDTO dto) {

    UsuarioResponseDTO usuarioActualizado = usuarioService.updateUserRoles(id, dto);
    return ResponseEntity.ok(usuarioActualizado);
  }

  @PostMapping("/elementos")
  public ResponseEntity<ElementoResponseDTO> crearElementoOficial(
      @Valid @RequestBody ElementoFormDTO dto,
      @AuthenticationPrincipal CustomUserDetails currentUser) {

    ElementoResponseDTO nuevoElemento =
        elementoService.crearElementoOficial(dto, currentUser.getId());
    return new ResponseEntity<>(nuevoElemento, HttpStatus.CREATED);
  }

  @PutMapping("/elementos/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MODERADOR')")
  public ResponseEntity<ElementoResponseDTO> updateElemento(
      @PathVariable Long id, @Valid @RequestBody ElementoFormDTO dto) {

    ElementoResponseDTO elementoActualizado = elementoService.updateElemento(id, dto);
    return ResponseEntity.ok(elementoActualizado);
  }

  @PutMapping("/elementos/{id}/oficializar")
  public ResponseEntity<ElementoResponseDTO> oficializarElemento(@PathVariable Long id) {

    ElementoResponseDTO elementoActualizado = elementoService.oficializarElemento(id);
    return ResponseEntity.ok(elementoActualizado);
  }

  @PutMapping("/elementos/{id}/comunitarizar")
  public ResponseEntity<ElementoResponseDTO> comunitarizarElemento(@PathVariable Long id) {

    ElementoResponseDTO elementoActualizado = elementoService.comunitarizarElemento(id);
    return ResponseEntity.ok(elementoActualizado);
  }
}
