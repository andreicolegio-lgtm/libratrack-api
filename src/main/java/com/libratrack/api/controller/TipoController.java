package com.libratrack.api.controller;

import com.libratrack.api.entity.Tipo;
import com.libratrack.api.service.TipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tipos") // Usamos /admin/ para estas rutas
public class TipoController {

    @Autowired
    private TipoService tipoService;

    /**
     * Endpoint para obtener todos los Tipos.
     * URL: GET /api/admin/tipos
     */
    @GetMapping
    public ResponseEntity<List<Tipo>> getAllTipos() {
        return ResponseEntity.ok(tipoService.getAllTipos());
    }

    /**
     * Endpoint para crear un nuevo Tipo (SOLO ADMINS).
     * URL: POST /api/admin/tipos
     * Cuerpo: { "nombre": "Anime" }
     */
    @PostMapping
    public ResponseEntity<?> createTipo(@RequestBody Tipo tipo) {
        // NOTA: En el futuro, protegeremos esta ruta para que solo
        // los Admins puedan usarla. Por ahora, está abierta.
        try {
            Tipo nuevoTipo = tipoService.createTipo(tipo);
            return new ResponseEntity<>(nuevoTipo, HttpStatus.CREATED); // 201 Created
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }
}