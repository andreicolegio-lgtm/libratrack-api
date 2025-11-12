package com.libratrack.api.controller;

import com.libratrack.api.dto.LoginResponseDTO;
import com.libratrack.api.dto.UsuarioResponseDTO; // <-- ¡NUEVA IMPORTACIÓN!
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.repository.UsuarioRepository;
import com.libratrack.api.service.UsuarioService;
import com.libratrack.api.service.jwt.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * --- ¡ACTUALIZADO (Sprint 4)! ---
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Endpoint para registrar un nuevo usuario (RF01).
     * --- ¡CORREGIDO! Ahora devuelve un DTO. ---
     */
    @PostMapping("/register") 
    public ResponseEntity<UsuarioResponseDTO> registerUser(@Valid @RequestBody Usuario usuario) { 
        
        // 1. Llama al servicio (que ahora devuelve un DTO)
        UsuarioResponseDTO usuarioRegistrado = usuarioService.registrarUsuario(usuario);
        
        // 2. Devuelve 201 Created (el DTO ya es seguro, no contiene password)
        return new ResponseEntity<>(usuarioRegistrado, HttpStatus.CREATED); 
    }

    /**
     * Endpoint para el login de usuario (RF02).
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        // ... (código sin cambios) ...
        String email = loginRequest.getOrDefault("email", "").trim();
        String password = loginRequest.getOrDefault("password", "").trim();

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("El email y la contraseña no pueden estar vacíos", HttpStatus.BAD_REQUEST);
        }

        try {
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario o contraseña incorrectos"));
            String username = usuario.getUsername();
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(authToken);
            String token = jwtService.generateToken(username);
            return new ResponseEntity<>(new LoginResponseDTO(token), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            logger.warn("Login failed for email: {}", email);
            return new ResponseEntity<>("Usuario o contraseña incorrectos", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Unexpected error during login", e);
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}