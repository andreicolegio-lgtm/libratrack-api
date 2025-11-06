package com.libratrack.api.controller;

import com.libratrack.api.entity.Genero; 
import com.libratrack.api.service.GeneroService; 
import jakarta.validation.Valid; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Géneros de contenido.
 */
@RestController
@RequestMapping("/api/admin/generos") 
@PreAuthorize("hasAuthority('ROLE_MODERADOR')") // ¡Protege toda la clase por defecto!
public class GeneroController {

    @Autowired
    private GeneroService generoService;

    /**
     * Endpoint para obtener todos los Géneros existentes (ADMIN).
     * Escucha en: GET /api/admin/generos
     * (Hereda la seguridad de ROLE_MODERADOR)
     */
    @GetMapping
    public ResponseEntity<List<Genero>> getAllGeneros() {
        return ResponseEntity.ok(generoService.getAllGeneros());
    }

    /**
     * Endpoint para crear un nuevo Genero (ADMIN).
     * Escucha en: POST /api/admin/generos
     * (Hereda la seguridad de ROLE_MODERADOR)
     */
    @PostMapping
    public ResponseEntity<?> createGenero(@Valid @RequestBody Genero genero) {
        try {
            Genero nuevoGenero = generoService.createGenero(genero);
            return new ResponseEntity<>(nuevoGenero, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}