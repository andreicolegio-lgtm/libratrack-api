package com.libratrack.api.controller;

import com.libratrack.api.entity.Tipo;
import com.libratrack.api.service.TipoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Tipos de contenido.
 */
@RestController
@RequestMapping("/api/admin/tipos") 
@PreAuthorize("hasAuthority('ROLE_MODERADOR')") // ¡Protege toda la clase por defecto!
public class TipoController {

    @Autowired
    private TipoService tipoService;

    /**
     * Endpoint para obtener todos los Tipos existentes (ADMIN).
     * Escucha en: GET /api/admin/tipos
     * (Hereda la seguridad de ROLE_MODERADOR)
     */
    @GetMapping
    public ResponseEntity<List<Tipo>> getAllTipos() {
        return ResponseEntity.ok(tipoService.getAllTipos());
    }

    /**
     * Endpoint para crear un nuevo Tipo (ADMIN).
     * Escucha en: POST /api/admin/tipos
     * (Hereda la seguridad de ROLE_MODERADOR)
     */
    @PostMapping
    public ResponseEntity<?> createTipo(@Valid @RequestBody Tipo tipo) {
        try {
            Tipo nuevoTipo = tipoService.createTipo(tipo);
            return new ResponseEntity<>(nuevoTipo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}