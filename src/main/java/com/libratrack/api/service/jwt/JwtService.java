package com.libratrack.api.service.jwt;

import com.libratrack.api.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/** Servicio utilitario para la generación, firma y validación de tokens JWT (JSON Web Tokens). */
@Service
public class JwtService {

  @Value("${libratrack.app.jwtSecret}")
  private String secret;

  @Value("${libratrack.app.jwtAccessExpirationMs}")
  private Long jwtAccessExpirationMs;

  /**
   * Genera un nuevo token de acceso para un usuario autenticado.
   *
   * @param userId ID del usuario que será el 'subject' del token.
   */
  public String generateToken(Long userId) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userId.toString());
  }

  private String createToken(Map<String, Object> claims, String userId) {
    return Jwts.builder()
        .claims(claims)
        .subject(userId)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + jwtAccessExpirationMs))
        .signWith(getSignKey())
        .compact();
  }

  private SecretKey getSignKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /** Extrae el ID de usuario (subject) del token. */
  public Long extractUserId(String token) {
    String sub = extractClaim(token, Claims::getSubject);
    return Long.parseLong(sub);
  }

  /** Valida que el token sea auténtico, no haya expirado y pertenezca al usuario proporcionado. */
  public Boolean validateToken(String token, UserDetails userDetails) {
    final Long userId = extractUserId(token);

    if (userDetails instanceof CustomUserDetails) {
      return userId.equals(((CustomUserDetails) userDetails).getId()) && !isTokenExpired(token);
    }

    // Fallback de seguridad (no debería ocurrir en producción normal)
    return false;
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();
  }
}
