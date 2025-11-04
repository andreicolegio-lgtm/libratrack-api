package com.libratrack.api.controller;

import com.libratrack.api.dto.ElementoResponseDTO;
import com.libratrack.api.dto.PropuestaResponseDTO;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.repository.UsuarioRepository;
import com.libratrack.api.service.PropuestaElementoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/moderacion")
public class ModeracionController {

    @Autowired
    private PropuestaElementoService propuestaService;

    @Autowired
    private UsuarioRepository usuarioRepo;

    /**
     * Endpoint para obtener la cola de propuestas pendientes (RF14).
     * (Solo accesible para Moderadores/Admins).
     * URL: GET /api/moderacion/pendientes
     */
    @PreAuthorize("hasAuthority('ROLE_MODERADOR')")
    @GetMapping("/pendientes")
    public ResponseEntity<List<PropuestaResponseDTO>> getPropuestasPendientes() {
        List<PropuestaResponseDTO> propuestas = propuestaService.getPropuestasPendientes();
        return ResponseEntity.ok(propuestas);
    }

    /**
     * Endpoint para aprobar una propuesta (RF15).
     * (Solo accesible para Moderadores/Admins).
     * URL: POST /api/moderacion/aprobar/1 (donde 1 es el ID de la propuesta)
     */
    @PreAuthorize("hasAuthority('ROLE_MODERADOR')")
    @PostMapping("/aprobar/{propuestaId}")
    public ResponseEntity<?> aprobarPropuesta(@PathVariable Long propuestaId, Principal principal) {

        try {
            // Obtenemos el nombre del moderador desde el token
            String revisorUsername = principal.getName();

            // Buscamos al moderador en la BD
            Usuario revisor = usuarioRepo.findByUsername(revisorUsername)
                    .orElseThrow(() -> new Exception("Token de revisor inválido."));

            // ¡Usamos el ID real!
            Long revisorId = revisor.getId(); 

            ElementoResponseDTO nuevoElemento = propuestaService.aprobarPropuesta(propuestaId, revisorId);
            return ResponseEntity.ok(nuevoElemento);
        } catch (Exception e) {
            // Manejo de errores
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // (Aquí iría el endpoint para rechazar propuestas)
}