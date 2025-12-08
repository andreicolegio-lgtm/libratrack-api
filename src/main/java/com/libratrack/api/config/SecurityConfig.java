package com.libratrack.api.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.libratrack.api.config.filter.JwtAuthFilter;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Clase principal de configuración de seguridad de Spring Security. Define las reglas de acceso,
 * filtros, encriptación y CORS.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita anotaciones @PreAuthorize en controladores
public class SecurityConfig {

  private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

  private final JwtAuthFilter jwtAuthFilter;

  @Autowired
  public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
  }

  /** Bean para encriptar contraseñas usando BCrypt (estándar robusto). */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /** Expone el AuthenticationManager de Spring para usarlo en el AuthController (login). */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * Configuración de CORS (Cross-Origin Resource Sharing). Permite que el frontend (Flutter/Web)
   * haga peticiones a la API desde dominios distintos.
   */
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Orígenes permitidos (Localhost para web, 10.0.2.2 para emulador Android)
    configuration.setAllowedOrigins(
        Arrays.asList(
            "http://localhost",
            "http://localhost:8080",
            "http://localhost:3000", // React/Next default
            "http://localhost:5000", // Flutter Web default
            "http://10.0.2.2", // Emulador Android Loopback
            "http://10.0.2.2:8080",
            "192.168.1.190",
            "192.168.1.190:8080"));

    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    configuration.setAllowedHeaders(
        Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
    configuration.setAllowCredentials(true); // Permitir cookies/credenciales si fuera necesario

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  /**
   * Cadena de filtros de seguridad. Aquí se define qué rutas son públicas y cuáles requieren
   * autenticación.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // Deshabilitar CSRF porque usamos JWT (stateless)
        .csrf(csrf -> csrf.disable())
        // Activar CORS con nuestra configuración personalizada
        .cors(withDefaults())
        // Configurar reglas de autorización de rutas
        .authorizeHttpRequests(
            auth ->
                auth
                    // Rutas públicas de autenticación
                    .requestMatchers("/api/auth/**")
                    .permitAll()
                    // Rutas públicas para swagger/docs (opcional, buena práctica tenerlas)
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**")
                    .permitAll()
                    // Cualquier otra petición requiere estar autenticado
                    .anyRequest()
                    .authenticated())
        // Configurar gestión de sesiones como STATELESS (sin estado, cada petición debe llevar
        // token)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Añadir nuestro filtro JWT antes del filtro de autenticación estándar
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    logger.info("Configuración de seguridad cargada correctamente.");
    return http.build();
  }
}
