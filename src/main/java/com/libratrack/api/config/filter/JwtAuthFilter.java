package com.libratrack.api.config.filter;

import com.libratrack.api.service.UserDetailsServiceImpl;
import com.libratrack.api.service.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Le dice a Spring que esta es una clase que debe gestionar
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    /**
     * Este es el método que intercepta todas las peticiones.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extraer la cabecera "Authorization"
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2. Comprobar si es un token "Bearer"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Quita el "Bearer "
            try {
                username = jwtService.extractUsername(token);
            } catch (Exception e) {
                // (Opcional: registrar el error de token inválido)
                System.err.println("Token JWT inválido o caducado: " + e.getMessage());
            }
        }

        // 3. Si tenemos un usuario y NO está ya autenticado...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 4. Cargamos los detalles del usuario desde la BD
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

            // 5. Validamos el token
            if (jwtService.validateToken(token, userDetails)) {
                
                // 6. Si es válido, creamos la autenticación para Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // No pasamos credenciales (contraseña)
                        userDetails.getAuthorities() // Pasamos los roles
                );
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 7. Guardamos la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 8. Continuamos con el resto de filtros
        filterChain.doFilter(request, response);
    }
}