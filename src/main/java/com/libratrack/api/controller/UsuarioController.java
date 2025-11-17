package com.libratrack.api.controller;

import com.libratrack.api.dto.PasswordChangeDTO;
import com.libratrack.api.dto.UsuarioResponseDTO;
import com.libratrack.api.dto.UsuarioUpdateDTO;
import com.libratrack.api.service.UsuarioService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

  @Autowired private UsuarioService usuarioService;

  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UsuarioResponseDTO> getMiPerfil(Principal principal) {
    Long userId = Long.parseLong(principal.getName());
    UsuarioResponseDTO perfil = usuarioService.getMiPerfilById(userId);
    return ResponseEntity.ok(perfil);
  }

  @PutMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UsuarioResponseDTO> updateMiPerfil(
      Principal principal, @Valid @RequestBody UsuarioUpdateDTO updateDto) {
    Long userId = Long.parseLong(principal.getName());
    UsuarioResponseDTO perfilActualizado = usuarioService.updateMiPerfilById(userId, updateDto);
    return ResponseEntity.ok(perfilActualizado);
  }

  @PutMapping("/me/password")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<String> updateMyPassword(
      Principal principal, @Valid @RequestBody PasswordChangeDTO passwordDto) {
    Long userId = Long.parseLong(principal.getName());
    usuarioService.changePasswordById(userId, passwordDto);
    return ResponseEntity.ok().body("Contraseña actualizada con éxito.");
  }

  @PutMapping("/me/foto")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UsuarioResponseDTO> updateFotoPerfil(
      Principal principal, @RequestBody Map<String, String> body) {
    Long userId = Long.parseLong(principal.getName());
    String fotoUrl = body.get("url");
    if (fotoUrl == null || fotoUrl.isBlank()) {
      return ResponseEntity.badRequest().build();
    }
    UsuarioResponseDTO perfilActualizado = usuarioService.updateFotoPerfilById(userId, fotoUrl);
    return ResponseEntity.ok(perfilActualizado);
  }
}
