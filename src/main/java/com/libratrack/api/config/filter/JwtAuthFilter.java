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

        // --- DEBUG 1: ¿Se está ejecutando el filtro? ---
        System.out.println("\n--- [DEBUG JWT FILTER] ---");
        System.out.println(">>> Petición recibida para: " + request.getRequestURI());

        // 1. Extraer la cabecera "Authorization"
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2. Comprobar si es un token "Bearer"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Quita el "Bearer "
            System.out.println(">>> Token Bearer encontrado.");
            try {
                username = jwtService.extractUsername(token);
                // --- DEBUG 2: ¿Se ha podido leer el token? ---
                System.out.println(">>> Usuario extraído del token: " + username);
            } catch (Exception e) {
                System.err.println("!!! ERROR AL PARSEAR EL TOKEN: " + e.getMessage());
            }
        } else {
            System.out.println(">>> No se ha encontrado cabecera 'Bearer'.");
        }

        // 3. Si tenemos un usuario y NO está ya autenticado...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println(">>> Usuario no autenticado, procediendo a cargar UserDetails...");
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

            // 5. Validamos el token
            if (jwtService.validateToken(token, userDetails)) {
                System.out.println(">>> ¡Token válido!");
                // 6. Creamos la autenticación para Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // No pasamos credenciales (contraseña)
                        userDetails.getAuthorities() // Pasamos los roles
                );
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 7. Guardamos la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println(">>> Usuario autenticado y guardado en SecurityContext.");
            } else {
                System.err.println("!!! Token inválido (validación fallida).");
            }
        }
        
        System.out.println("--- [FIN DEBUG JWT FILTER] ---\n");
        // 8. Continuamos con el resto de filtros
        filterChain.doFilter(request, response);
    }
}