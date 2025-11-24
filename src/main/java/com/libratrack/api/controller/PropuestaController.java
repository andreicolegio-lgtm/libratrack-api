package com.libratrack.api.controller;

import com.libratrack.api.dto.PropuestaRequestDTO;
import com.libratrack.api.dto.PropuestaResponseDTO;
import com.libratrack.api.security.CustomUserDetails;
import com.libratrack.api.service.PropuestaElementoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/propuestas")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class PropuestaController {

  @Autowired private PropuestaElementoService propuestaService;

  @PostMapping
  public ResponseEntity<PropuestaResponseDTO> createPropuesta(
      @Valid @RequestBody PropuestaRequestDTO dto,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    PropuestaResponseDTO nuevaPropuesta =
        propuestaService.createPropuesta(dto, currentUser.getId());
    return new ResponseEntity<>(nuevaPropuesta, HttpStatus.CREATED);
  }
}
