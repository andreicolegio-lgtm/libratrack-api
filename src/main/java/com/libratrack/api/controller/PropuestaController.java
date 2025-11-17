package com.libratrack.api.controller;

import com.libratrack.api.dto.PropuestaRequestDTO;
import com.libratrack.api.dto.PropuestaResponseDTO;
import com.libratrack.api.service.PropuestaElementoService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/propuestas")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class PropuestaController {

  @Autowired private PropuestaElementoService propuestaService;

  @PostMapping
  public ResponseEntity<PropuestaResponseDTO> createPropuesta(
      @Valid @RequestBody PropuestaRequestDTO dto, Principal principal) {

    Long userId = Long.parseLong(principal.getName());

    PropuestaResponseDTO nuevaPropuesta = propuestaService.createPropuesta(dto, userId);

    return new ResponseEntity<>(nuevaPropuesta, HttpStatus.CREATED);
  }
}
