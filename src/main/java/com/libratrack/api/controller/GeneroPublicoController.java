package com.libratrack.api.controller;

import com.libratrack.api.entity.Genero;
import com.libratrack.api.service.GeneroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para la consulta pública de Géneros de contenido (RF09).
 */
@RestController
@RequestMapping("/api/generos") // Ruta base CLARA y simple
public class GeneroPublicoController {

    @Autowired
    private GeneroService generoService;

    /**
     * Endpoint para obtener todos los Géneros existentes.
     * Escucha en: GET /api/generos
     *
     * Seguridad: Requiere cualquier usuario autenticado (ROLE_USER o MODERADOR).
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Genero>> getAllGenerosPublico() {
        return ResponseEntity.ok(generoService.getAllGeneros());
    }
}