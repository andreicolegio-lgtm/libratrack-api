package com.libratrack.api.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.libratrack.api.dto.GoogleTokenDTO;
import com.libratrack.api.dto.LoginResponseDTO;
import com.libratrack.api.dto.UsuarioResponseDTO;
import com.libratrack.api.entity.RefreshToken;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ConflictException;
import com.libratrack.api.exception.TokenRefreshException;
import com.libratrack.api.repository.UsuarioRepository;
import com.libratrack.api.service.RefreshTokenService;
import com.libratrack.api.service.UsuarioService;
import com.libratrack.api.service.jwt.JwtService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador público para autenticación y registro de usuarios. Maneja Login (Email/Pass y
 * Google), Registro y Refresco de Tokens.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @Value("${libratrack.app.googleWebClientId}")
  private String googleWebClientId;

  @Autowired private UsuarioService usuarioService;
  @Autowired private JwtService jwtService;
  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private UsuarioRepository usuarioRepository;
  @Autowired private RefreshTokenService refreshTokenService;

  /** Inicio de sesión o Registro mediante Google Identity Services. */
  @PostMapping("/google")
  public ResponseEntity<?> loginWithGoogle(@Valid @RequestBody GoogleTokenDTO googleTokenDTO) {
    GoogleIdTokenVerifier verifier =
        new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
            .setAudience(Collections.singletonList(googleWebClientId))
            .build();

    try {
      GoogleIdToken idToken = verifier.verify(googleTokenDTO.getToken());
      if (idToken == null) {
        logger.warn("Intento de login con token de Google inválido.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "{exception.auth.google_token_invalid}"));
      }

      GoogleIdToken.Payload payload = idToken.getPayload();
      Usuario usuario = usuarioService.findOrCreateGoogleUser(payload);

      String accessToken = jwtService.generateToken(usuario.getId());
      RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario.getUsername());

      logger.info("Login exitoso (Google) para: {}", usuario.getEmail());
      return ResponseEntity.ok(new LoginResponseDTO(accessToken, refreshToken.getToken()));

    } catch (GeneralSecurityException | IOException e) {
      logger.error("Error verificando token Google", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "{exception.auth.google_verify_error}"));
    }
  }

  /** Registro de nuevo usuario con Email y Contraseña. */
  @PostMapping("/register")
  public ResponseEntity<UsuarioResponseDTO> registerUser(@Valid @RequestBody Usuario usuario) {
    UsuarioResponseDTO usuarioRegistrado = usuarioService.registrarUsuario(usuario);
    return new ResponseEntity<>(usuarioRegistrado, HttpStatus.CREATED);
  }

  /** Inicio de sesión clásico con Email y Contraseña. */
  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
    String email = loginRequest.getOrDefault("email", "").trim();
    String password = loginRequest.getOrDefault("password", "").trim();

    if (email.isEmpty() || password.isEmpty()) {
      throw new ConflictException("{exception.auth.credentials_empty}");
    }

    try {
      // 1. Buscar usuario por email para obtener su username (necesario para Spring Security)
      Usuario usuario =
          usuarioRepository
              .findByEmail(email)
              .orElseThrow(() -> new UsernameNotFoundException("User not found"));

      // 2. Autenticar con Spring Security
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(usuario.getUsername(), password));

      // 3. Generar Tokens
      String accessToken = jwtService.generateToken(usuario.getId());
      RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario.getUsername());

      return ResponseEntity.ok(new LoginResponseDTO(accessToken, refreshToken.getToken()));

    } catch (UsernameNotFoundException | BadCredentialsException e) {
      logger.warn("Fallo de autenticación para email: {}", email);
      throw new ConflictException("{exception.auth.credentials_invalid}");
    } catch (Exception e) {
      logger.error("Error inesperado en login", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "{exception.internal_server_error}"));
    }
  }

  /** Renueva el Access Token utilizando un Refresh Token válido. */
  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
    String requestRefreshToken = request.get("refreshToken");

    if (requestRefreshToken == null || requestRefreshToken.isBlank()) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "{exception.auth.refresh_token_required}"));
    }

    try {
      RefreshToken refreshToken =
          refreshTokenService
              .findByToken(requestRefreshToken)
              .orElseThrow(
                  () ->
                      new TokenRefreshException(
                          requestRefreshToken, "{exception.token.refresh.not_found}"));

      refreshTokenService.verifyExpiration(refreshToken);

      Usuario usuario = refreshToken.getUsuario();
      String newAccessToken = jwtService.generateToken(usuario.getId());

      return ResponseEntity.ok(new LoginResponseDTO(newAccessToken, requestRefreshToken));

    } catch (TokenRefreshException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
    }
  }

  /** Cierra sesión invalidando el Refresh Token. */
  @PostMapping("/logout")
  public ResponseEntity<?> logoutUser(@RequestBody Map<String, String> request) {
    String requestRefreshToken = request.get("refreshToken");
    if (requestRefreshToken != null && !requestRefreshToken.isBlank()) {
      refreshTokenService.deleteByToken(requestRefreshToken);
    }
    return ResponseEntity.ok(Map.of("message", "{message.logout_successful}"));
  }
}
