package com.libratrack.api.controller;

import com.libratrack.api.dto.ElementoRelacionDTO;
import com.libratrack.api.dto.ElementoResponseDTO;
import com.libratrack.api.service.ElementoService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/elementos")
public class ElementoController {

  @Autowired private ElementoService elementoService;

  @PreAuthorize("hasAuthority('ROLE_USER')")
  @GetMapping
  public ResponseEntity<Page<ElementoResponseDTO>> getAllElementos(
      @RequestParam(value = "search", required = false) String searchText,
      @RequestParam(value = "tipo", required = false) String tipoName,
      @RequestParam(value = "genero", required = false) String generoName,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("titulo").ascending());
    Page<ElementoResponseDTO> pagina =
        elementoService.findAllElementos(pageable, searchText, tipoName, generoName);
    return ResponseEntity.ok(pagina);
  }

  @PreAuthorize("hasAuthority('ROLE_USER')")
  @GetMapping("/{id}")
  public ResponseEntity<ElementoResponseDTO> getElementoById(@PathVariable Long id) {

    Optional<ElementoResponseDTO> elementoDTOOptional = elementoService.findElementoById(id);

    if (elementoDTOOptional.isPresent()) {
      return ResponseEntity.ok(elementoDTOOptional.get());
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PreAuthorize("hasAnyAuthority('ROLE_MODERADOR', 'ROLE_ADMIN')")
  @GetMapping("/all-simple")
  public ResponseEntity<List<ElementoRelacionDTO>> getAllElementosSimple() {
    List<ElementoRelacionDTO> lista = elementoService.findAllSimple();
    return ResponseEntity.ok(lista);
  }
}
