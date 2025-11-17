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

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

  @Autowired private JwtService jwtService;

  @Autowired private UserDetailsServiceImpl userDetailsServiceImpl;

  @Autowired private ObjectMapper objectMapper;

  private void sendJsonError(
      HttpServletResponse response, HttpStatus status, String errorKey, String message)
      throws IOException {
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", new Date());
    body.put("status", status.value());
    body.put("error", errorKey);
    body.put("message", message);

    response.getWriter().write(objectMapper.writeValueAsString(body));
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String token;
    final Long userId;

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      logger.debug("Authorization header is missing or does not start with 'Bearer '");
      filterChain.doFilter(request, response);
      return;
    }

    token = authHeader.substring(7);

    try {
      userId = jwtService.extractUserId(token);
    } catch (ExpiredJwtException e) {
      logger.warn("JWT Token has expired: {}", e.getMessage());
      sendJsonError(response, HttpStatus.UNAUTHORIZED, "E_TOKEN_EXPIRED", "Token JWT caducado");
      return;
    } catch (Exception e) {
      logger.warn("Failed to extract userId from token: {}", e.getMessage());
      sendJsonError(response, HttpStatus.UNAUTHORIZED, "E_TOKEN_INVALID", "Token JWT inválido");
      return;
    }

    if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userDetailsServiceImpl.loadUserById(userId);
      if (jwtService.validateToken(token, userDetails)) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        logger.info("Authentication successful for userId: {}", userId);
      } else {
        logger.warn("Token validation failed for userId: {}", userId);
      }
    }

    filterChain.doFilter(request, response);
  }
}