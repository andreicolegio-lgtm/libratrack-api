package com.libratrack.api.controller;

import com.libratrack.api.dto.ElementoResponseDTO; 
import com.libratrack.api.service.ElementoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.web.bind.annotation.*;

// NUEVAS IMPORTACIONES
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// import java.util.List; // Ya no devolvemos una List
import java.util.Optional;

@RestController
@RequestMapping("/api/elementos") 
public class ElementoController {

    @Autowired 
    private ElementoService elementoService;

    /**
     * Endpoint para obtener todos los elementos o para buscar por título (RF09).
     * Escucha en: GET /api/elementos?search=texto&tipo=...&genero=...&page=0&size=20
     *
     * --- ¡REFACTORIZADO PARA PAGINACIÓN! ---
     *
     * @param searchText El parámetro de búsqueda (opcional).
     * @param tipoName El filtro de tipo (opcional).
     * @param generoName El filtro de género (opcional).
     * @param page El número de página (por defecto 0).
     * @param size El tamaño de la página (por defecto 20).
     * @return ResponseEntity con una 'Page' (Página) de DTOs de los elementos (200 OK).
     */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public ResponseEntity<Page<ElementoResponseDTO>> getAllElementos(
            @RequestParam(value = "search", required = false) String searchText,
            @RequestParam(value = "tipo", required = false) String tipoName,
            @RequestParam(value = "genero", required = false) String generoName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
        ) {

        // 1. Creamos el objeto Pageable que el servicio espera
        // (Ordenamos por título por defecto, puedes cambiarlo)
        Pageable pageable = PageRequest.of(page, size, Sort.by("titulo").ascending());

        // 2. Llama al servicio, pasándole todos los parámetros
        Page<ElementoResponseDTO> pagina = elementoService.findAllElementos(pageable, searchText, tipoName, generoName);
        
        return ResponseEntity.ok(pagina); // Devuelve 200 OK con el objeto Page
    }

    /**
     * Endpoint para obtener un elemento por su ID (RF10: Ficha Detallada).
     * (Sin cambios)
     */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getElementoById(@PathVariable Long id) {
        
        Optional<ElementoResponseDTO> elementoDTOOptional = elementoService.findElementoById(id);

        if (elementoDTOOptional.isPresent()) {
            return ResponseEntity.ok(elementoDTOOptional.get()); // 200 OK
        } else {
            return new ResponseEntity<>("Elemento no encontrado con id: " + id, HttpStatus.NOT_FOUND); // 404
        }
    }
}