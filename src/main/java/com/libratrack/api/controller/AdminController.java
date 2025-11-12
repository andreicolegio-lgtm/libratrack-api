package com.libratrack.api.controller;

import com.libratrack.api.dto.ElementoFormDTO; 
import com.libratrack.api.dto.ElementoResponseDTO; 
import com.libratrack.api.dto.RolUpdateDTO;
import com.libratrack.api.dto.UsuarioResponseDTO;
import com.libratrack.api.service.ElementoService; 
import com.libratrack.api.service.UsuarioService;
import jakarta.validation.Valid;

// --- ¡NUEVAS IMPORTACIONES! ---
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
// ---

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

/**
 * --- ¡ACTUALIZADO (Sprint 7)! ---
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')") 
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private ElementoService elementoService; 

    // ======================================================
    // GESTIÓN DE USUARIOS (Petición B, C, G, 14)
    // ======================================================

    /**
     * --- ¡REFACTORIZADO (Sprint 7)! ---
     * Endpoint para que un Admin obtenga la lista de TODOS los usuarios
     * con paginación, búsqueda y filtros.
     * Escucha en: GET /api/admin/usuarios?page=0&size=20&search=texto&role=MODERADOR
     */
    @GetMapping("/usuarios")
    public ResponseEntity<Page<UsuarioResponseDTO>> getAllUsuarios(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "role", required = false) String roleFilter) {
        
        // Creamos el objeto Pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());

        // Llamamos al servicio refactorizado
        Page<UsuarioResponseDTO> usuarios = usuarioService.getAllUsuarios(pageable, search, roleFilter);
        
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Endpoint para que un Admin actualice los roles de cualquier usuario.
     * Escucha en: PUT /api/admin/usuarios/{id}/roles
     */
    @PutMapping("/usuarios/{id}/roles")
    public ResponseEntity<UsuarioResponseDTO> updateUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody RolUpdateDTO dto) {
        
        UsuarioResponseDTO usuarioActualizado = usuarioService.updateUserRoles(id, dto);
        return ResponseEntity.ok(usuarioActualizado);
    }
    
    // ======================================================
    // GESTIÓN DE CONTENIDO (Petición 8, 15, 17, F)
    // ======================================================

    // ... (Endpoints /elementos, /elementos/{id}, /elementos/{id}/oficializar, /elementos/{id}/comunitarizar ... sin cambios) ...
    
    @PostMapping("/elementos")
    public ResponseEntity<ElementoResponseDTO> crearElementoOficial(
            @Valid @RequestBody ElementoFormDTO dto, 
            Principal principal) {
        
        ElementoResponseDTO nuevoElemento = elementoService.crearElementoOficial(dto, principal.getName());
        return new ResponseEntity<>(nuevoElemento, HttpStatus.CREATED);
    }
    
    @PutMapping("/elementos/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MODERADOR')") 
    public ResponseEntity<ElementoResponseDTO> updateElemento(
            @PathVariable Long id,
            @Valid @RequestBody ElementoFormDTO dto) {
        
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