package com.libratrack.api.controller;

import com.libratrack.api.entity.Genero;
import com.libratrack.api.service.GeneroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/generos") // Usamos /admin/ para estas rutas
public class GeneroController {

    @Autowired
    private GeneroService generoService;

    /**
     * Endpoint para obtener todos los Géneros.
     * URL: GET /api/admin/generos
     */
    @GetMapping
    public ResponseEntity<List<Genero>> getAllGeneros() {
        return ResponseEntity.ok(generoService.getAllGeneros());
    }

    /**
     * Endpoint para crear un nuevo Genero (SOLO ADMINS).
     * URL: POST /api/admin/generos
     * Cuerpo: { "nombre": "Ciencia Ficción" }
     */
    @PostMapping
    public ResponseEntity<?> createGenero(@RequestBody Genero genero) {
        // NOTA: También protegeremos esta ruta para Admins.
        try {
            Genero nuevoGenero = generoService.createGenero(genero);
            return new ResponseEntity<>(nuevoGenero, HttpStatus.CREATED); // 201 Created
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }
}