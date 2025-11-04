package com.libratrack.api.controller;

import com.libratrack.api.dto.LoginResponseDTO;
import com.libratrack.api.entity.Usuario; // Tu entidad Usuario
import com.libratrack.api.service.UsuarioService; // Tu servicio de Usuario
import com.libratrack.api.service.jwt.JwtService;

import jakarta.validation.Valid; // Para validar los objetos Usuario en la petición
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Para los códigos de estado HTTP
import org.springframework.http.ResponseEntity; // Para construir respuestas HTTP
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult; // Para manejar errores de validación
import org.springframework.web.bind.annotation.*; // Para las anotaciones REST

import java.util.HashMap;
import java.util.Map;

@RestController // Indica a Spring que esta clase es un controlador REST
@RequestMapping("/api/auth") // Todas las rutas de este controlador empezarán con /api/auth
public class AuthController {

    @Autowired // Inyecta tu servicio de usuario aquí
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Endpoint para registrar un nuevo usuario (RF01).
     *
     * URL: POST /api/auth/register
     * Cuerpo de la petición: { "username": "...", "email": "...", "password": "..." }
     *
     * @param usuario El objeto Usuario enviado en el cuerpo de la petición.
     * @param bindingResult Objeto para capturar errores de validación.
     * @return ResponseEntity con el usuario registrado o errores.
     */
    @PostMapping("/register") // Este método responderá a peticiones POST en /api/auth/register
    public ResponseEntity<?> registerUser(@Valid @RequestBody Usuario usuario, BindingResult bindingResult) {
        // 1. Manejar errores de validación (por ejemplo, si el email no es válido)
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); // Código 400
        }

        try {
            // 2. Llamar al servicio para registrar al usuario
            Usuario usuarioRegistrado = usuarioService.registrarUsuario(usuario);
            // Quitamos la contraseña de la respuesta por seguridad (aunque luego la cifremos)
            usuarioRegistrado.setPassword(null); 
            return new ResponseEntity<>(usuarioRegistrado, HttpStatus.CREATED); // Código 201 (Creado)
        } catch (Exception e) {
            // 3. Manejar errores del servicio (ej. email o username ya existen)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // Código 409 (Conflicto)
        }
    }

    /**
     * Endpoint para el login de usuario (RF02).
     * Devuelve un Token JWT si el login es exitoso.
     *
     * URL: POST /api/auth/login
     * Cuerpo de la petición: { "username": "...", "password": "..." }
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return new ResponseEntity<>("El nombre de usuario y la contraseña no pueden estar vacíos", HttpStatus.BAD_REQUEST);
        }

        try {
            // 1. Crear el "ticket" de autenticación con las credenciales
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

            // 2. Pedir al "Jefe de Seguridad" (Manager) que autentique el ticket
            // Esto llamará automáticamente a UserDetailsServiceImpl y al PasswordEncoder
            authenticationManager.authenticate(authToken);

            // 3. Si la línea anterior NO ha lanzado una excepción, el login es correcto.
            // Generamos el token JWT.
            String token = jwtService.generateToken(username);

            // 4. Devolvemos el token
            return new ResponseEntity<>(new LoginResponseDTO(token), HttpStatus.OK); // 200 OK

        } catch (Exception e) {
            // Si la autenticación falla (ej. BadCredentialsException),
            // el 'authenticationManager' lanzará una excepción.
            return new ResponseEntity<>("Usuario o contraseña incorrectos", HttpStatus.UNAUTHORIZED); // 401
        }
    }
}