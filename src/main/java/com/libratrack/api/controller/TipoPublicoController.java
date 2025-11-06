package com.libratrack.api.controller;

import com.libratrack.api.entity.Tipo;
import com.libratrack.api.service.TipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para la consulta pública de Tipos de contenido (RF09).
 * Esta clase es el endpoint que la aplicación Flutter debe llamar.
 */
@RestController
@RequestMapping("/api/tipos") // Ruta base CLARA y simple
public class TipoPublicoController {

    @Autowired
    private TipoService tipoService;

    /**
     * Endpoint para obtener todos los Tipos existentes.
     * Escucha en: GET /api/tipos
     *
     * Seguridad: Requiere cualquier usuario autenticado (ROLE_USER o MODERADOR).
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()") 
    public ResponseEntity<List<Tipo>> getAllTiposPublico() {
        return ResponseEntity.ok(tipoService.getAllTipos());
    }
}