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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador público (para usuarios registrados) para explorar el contenido de la plataforma.
 * Permite buscar, filtrar y ver detalles de los elementos.
 */
@RestController
@RequestMapping("/api/elementos")
public class ElementoController {

  @Autowired private ElementoService elementoService;

  /**
   * Busca elementos con paginación y filtros dinámicos.
   *
   * @param searchText Texto para buscar en el título (opcional).
   * @param types Lista de tipos a incluir (ej. "Anime", "Manga") (opcional).
   * @param genres Lista de géneros a incluir (opcional).
   * @param page Número de página (0-indexado).
   * @param size Tamaño de la página.
   * @return Página de resultados DTO.
   */
  @PreAuthorize("hasAuthority('ROLE_USER')")
  @GetMapping
  public ResponseEntity<Page<ElementoResponseDTO>> getAllElementos(
      @RequestParam(value = "search", required = false) String searchText,
      @RequestParam(value = "types", required = false) List<String> types,
      @RequestParam(value = "genres", required = false) List<String> genres,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("titulo").ascending());
    Page<ElementoResponseDTO> pagina =
        elementoService.findAllElementos(pageable, searchText, types, genres);
    return ResponseEntity.ok(pagina);
  }

  /** Obtiene los detalles completos de un elemento específico por su ID. */
  @PreAuthorize("hasAuthority('ROLE_USER')")
  @GetMapping("/{id}")
  public ResponseEntity<ElementoResponseDTO> getElementoById(@PathVariable Long id) {
    Optional<ElementoResponseDTO> elementoDTO = elementoService.findElementoById(id);
    return elementoDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Endpoint ligero para obtener una lista simple de todos los elementos (ID, Título, Imagen). Útil
   * para paneles de administración o selectores de relaciones. Restringido a personal autorizado.
   */
  @PreAuthorize("hasAnyAuthority('ROLE_MODERADOR', 'ROLE_ADMIN')")
  @GetMapping("/all-simple")
  public ResponseEntity<List<ElementoRelacionDTO>> getAllElementosSimple() {
    List<ElementoRelacionDTO> lista = elementoService.findAllSimple();
    return ResponseEntity.ok(lista);
  }
}
