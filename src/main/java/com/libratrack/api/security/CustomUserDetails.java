package com.libratrack.api.security;

import com.libratrack.api.entity.Usuario;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implementación personalizada de {@link UserDetails} de Spring Security.
 *
 * <p>Esta clase actúa como un adaptador (Wrapper) alrededor de nuestra entidad de dominio {@link
 * Usuario}. Permite que Spring Security entienda cómo acceder a la información crítica de seguridad
 * (contraseña, roles, estado de la cuenta) de nuestros usuarios.
 */
public class CustomUserDetails implements UserDetails {

  private Long id;
  private String username;
  private String email;
  private String password;
  private Collection<? extends GrantedAuthority> authorities;

  public CustomUserDetails(Usuario usuario) {
    this.id = usuario.getId();
    this.username = usuario.getUsername();
    this.email = usuario.getEmail();
    this.password = usuario.getPassword();

    // Conversión de roles booleanos a Authorities de Spring Security
    Set<GrantedAuthority> auths = new HashSet<>();

    // Rol base para todos
    auths.add(new SimpleGrantedAuthority("ROLE_USER"));

    // Roles jerárquicos
    if (usuario.esAdmin()) {
      auths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
      auths.add(new SimpleGrantedAuthority("ROLE_MODERADOR")); // Admin implica Moderador
    } else if (usuario.esMod()) {
      auths.add(new SimpleGrantedAuthority("ROLE_MODERADOR"));
    }

    this.authorities = auths;
  }

  /**
   * Devuelve el ID único del usuario en nuestra base de datos. Útil para lógica de negocio que
   * requiere el ID numérico.
   */
  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  // --- Métodos de la interfaz UserDetails ---

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  /**
   * Indica si la cuenta del usuario ha expirado.
   *
   * @return true si la cuenta es válida (no expirada).
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Indica si la cuenta está bloqueada o suspendida.
   *
   * @return true si la cuenta no está bloqueada.
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Indica si las credenciales (contraseña) han expirado.
   *
   * @return true si las credenciales son válidas.
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Indica si el usuario está habilitado o deshabilitado.
   *
   * @return true si el usuario está habilitado.
   */
  @Override
  public boolean isEnabled() {
    return true;
  }

  // --- Métodos Object ---

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CustomUserDetails that = (CustomUserDetails) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
