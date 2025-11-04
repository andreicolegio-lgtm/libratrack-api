package com.libratrack.api.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails; // Importante para la integración
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // --- 1. LA CLAVE SECRETA ---
    // Esta es la "firma" secreta de tu API. ¡NUNCA la compartas!
    // En un proyecto real, estaría en application.properties, no aquí.
    // Para tu TFG, está bien ponerla aquí.
    // (Generada aleatoriamente, puedes cambiarla por cualquier texto largo)
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    /**
     * Genera un nuevo Token JWT para un usuario.
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        // (Aquí podríamos añadir más "claims" como el rol del usuario)
        return createToken(claims, username);
    }

    /**
     * Método principal de creación de token.
     */
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims) // Información extra (payload)
                .setSubject(username) // El "dueño" del token (el usuario)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Cuándo se creó
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Caducidad: 10 horas
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // Firma con la clave secreta
                .compact();
    }

    // --- 2. MÉTODOS DE VALIDACIÓN Y LECTURA ---

    /**
     * Extrae el nombre de usuario (subject) del token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Comprueba si el token es válido (si no ha caducado y la firma es correcta).
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Comprueba si el token ha caducado.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de caducidad.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // --- 3. MÉTODOS AUXILIARES ---

    /**
     * Método genérico para extraer cualquier "claim" (información) del token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Decodifica el token completo usando la clave secreta.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Convierte la clave secreta (String) en un objeto Key que la librería puede usar.
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}