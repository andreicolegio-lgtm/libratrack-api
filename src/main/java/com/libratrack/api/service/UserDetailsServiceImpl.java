package com.libratrack.api.service;

import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ResourceNotFoundException;
import com.libratrack.api.repository.UsuarioRepository;
import com.libratrack.api.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio central para la carga de detalles de usuario en el contexto de Spring Security.
 *
 * <p>Implementa {@link UserDetailsService} para proporcionar la información necesaria durante los
 * procesos de autenticación y autorización.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired private UsuarioRepository usuarioRepository;

  /**
   * Carga los detalles del usuario basándose en su ID numérico.
   *
   * <p>Este método es utilizado principalmente por el filtro de autenticación JWT (JwtAuthFilter),
   * donde el token contiene el ID del usuario (subject).
   *
   * @param userId ID único del usuario.
   * @return Instancia de {@link UserDetails} con los datos y roles del usuario.
   * @throws UsernameNotFoundException Si no se encuentra un usuario con ese ID (envuelto en
   *     ResourceNotFoundException para consistencia REST).
   */
  @Transactional(readOnly = true)
  public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
    Usuario usuario =
        usuarioRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("{exception.auth.token.invalid}"));

    return new CustomUserDetails(usuario);
  }

  /**
   * Carga los detalles del usuario basándose en su nombre de usuario (o email, si se configura
   * así).
   *
   * <p>Este método es el estándar requerido por Spring Security y se utiliza durante el proceso de
   * inicio de sesión (Login) tradicional.
   *
   * @param username Nombre de usuario proporcionado en el formulario de login.
   * @return Instancia de {@link UserDetails} con los datos y roles del usuario.
   * @throws UsernameNotFoundException Si no se encuentra el usuario o las credenciales son
   *     incorrectas.
   */
  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuario usuario =
        usuarioRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("{exception.auth.credentials.invalid}"));

    return new CustomUserDetails(usuario);
  }
}
