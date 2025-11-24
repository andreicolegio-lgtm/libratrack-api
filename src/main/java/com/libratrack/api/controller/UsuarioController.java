package com.libratrack.api.controller;

import com.libratrack.api.dto.PasswordChangeDTO;
import com.libratrack.api.dto.UsuarioResponseDTO;
import com.libratrack.api.dto.UsuarioUpdateDTO;
import com.libratrack.api.security.CustomUserDetails;
import com.libratrack.api.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

  @Autowired private UsuarioService usuarioService;

  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UsuarioResponseDTO> getMiPerfil(
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    UsuarioResponseDTO perfil = usuarioService.getMiPerfilById(currentUser.getId());
    return ResponseEntity.ok(perfil);
  }

  @PutMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UsuarioResponseDTO> updateMiPerfil(
      @AuthenticationPrincipal CustomUserDetails currentUser,
      @Valid @RequestBody UsuarioUpdateDTO updateDto) {
    UsuarioResponseDTO perfilActualizado =
        usuarioService.updateMiPerfilById(currentUser.getId(), updateDto);
    return ResponseEntity.ok(perfilActualizado);
  }

  @PutMapping("/me/password")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<String> updateMyPassword(
      @AuthenticationPrincipal CustomUserDetails currentUser,
      @Valid @RequestBody PasswordChangeDTO passwordDto) {
    usuarioService.changePasswordById(currentUser.getId(), passwordDto);
    return ResponseEntity.ok().body("Contraseña actualizada con éxito.");
  }

  @PutMapping("/me/foto")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UsuarioResponseDTO> updateFotoPerfil(
      @AuthenticationPrincipal CustomUserDetails currentUser,
      @RequestBody Map<String, String> body) {
    String fotoUrl = body.get("url");
    if (fotoUrl == null || fotoUrl.isBlank()) {
      return ResponseEntity.badRequest().build();
    }
    UsuarioResponseDTO perfilActualizado =
        usuarioService.updateFotoPerfilById(currentUser.getId(), fotoUrl);
    return ResponseEntity.ok(perfilActualizado);
  }
}
