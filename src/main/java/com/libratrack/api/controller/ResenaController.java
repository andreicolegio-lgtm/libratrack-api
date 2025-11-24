package com.libratrack.api.controller;

import com.libratrack.api.dto.ResenaDTO;
import com.libratrack.api.dto.ResenaResponseDTO;
import com.libratrack.api.security.CustomUserDetails;
import com.libratrack.api.service.ResenaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resenas")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class ResenaController {

  @Autowired private ResenaService resenaService;

  @GetMapping("/elemento/{elementoId}")
  public ResponseEntity<List<ResenaResponseDTO>> getResenasDelElemento(
      @PathVariable Long elementoId) {
    List<ResenaResponseDTO> resenas = resenaService.getResenasByElementoId(elementoId);
    return ResponseEntity.ok(resenas);
  }

  @PostMapping
  public ResponseEntity<ResenaResponseDTO> createResena(
      @Valid @RequestBody ResenaDTO resenaDTO,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    ResenaResponseDTO nuevaResena = resenaService.createResena(resenaDTO, currentUser.getId());
    return new ResponseEntity<>(nuevaResena, HttpStatus.CREATED);
  }
}
