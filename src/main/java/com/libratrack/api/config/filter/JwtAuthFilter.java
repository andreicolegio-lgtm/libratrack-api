package com.libratrack.api.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libratrack.api.service.UserDetailsServiceImpl;
import com.libratrack.api.service.jwt.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro de seguridad que intercepta cada petición HTTP para validar el token JWT. Si el token es
 * válido, establece la autenticación en el contexto de seguridad de Spring.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

  @Autowired private JwtService jwtService;
  @Autowired private UserDetailsServiceImpl userDetailsServiceImpl;
  @Autowired private ObjectMapper objectMapper;

  /** Método principal del filtro. Extrae el token, lo valida y autentica al usuario. */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String token;
    final Long userId;

    // 1. Verificar si la cabecera Authorization existe y tiene el formato correcto
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    token = authHeader.substring(7); // Extraer token eliminando "Bearer "

    try {
      userId = jwtService.extractUserId(token);
    } catch (ExpiredJwtException e) {
      logger.warn("Token JWT expirado: {}", e.getMessage());
      sendJsonError(
          response,
          HttpStatus.UNAUTHORIZED,
          "{exception.auth.token_expired}",
          "El token de acceso ha caducado.");
      return; // Detener la cadena de filtros
    } catch (Exception e) {
      logger.error("Token JWT inválido o mal formado: {}", e.getMessage());
      sendJsonError(
          response,
          HttpStatus.UNAUTHORIZED,
          "{exception.auth.token_invalid}",
          "Token de acceso inválido.");
      return; // Detener la cadena de filtros
    }

    // 2. Si tenemos un ID de usuario y no hay autenticación previa en el contexto
    if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      // Cargar detalles del usuario desde la base de datos
      UserDetails userDetails = userDetailsServiceImpl.loadUserById(userId);

      // 3. Validar el token contra los detalles del usuario
      if (jwtService.validateToken(token, userDetails)) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Establecer la autenticación en el contexto (Usuario logueado para esta petición)
        SecurityContextHolder.getContext().setAuthentication(authToken);
        logger.debug("Usuario autenticado exitosamente: ID {}", userId);
      } else {
        logger.warn("Validación de token fallida para el usuario ID: {}", userId);
      }
    }

    // Continuar con el siguiente filtro en la cadena
    filterChain.doFilter(request, response);
  }

  /**
   * Envía una respuesta de error en formato JSON estandarizado. Útil para errores que ocurren
   * dentro del filtro antes de llegar a los controladores.
   */
  private void sendJsonError(
      HttpServletResponse response, HttpStatus status, String errorKey, String message)
      throws IOException {
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", new Date());
    body.put("status", status.value());
    body.put("error", errorKey);
    body.put("message", message);

    response.getWriter().write(objectMapper.writeValueAsString(body));
  }
}
