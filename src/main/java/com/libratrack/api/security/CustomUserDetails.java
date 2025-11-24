package com.libratrack.api.security;

import com.libratrack.api.entity.Usuario;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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

    Set<GrantedAuthority> auths = new HashSet<>();
    auths.add(new SimpleGrantedAuthority("ROLE_USER"));
    if (usuario.esAdmin()) {
      auths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
      auths.add(new SimpleGrantedAuthority("ROLE_MODERADOR"));
    } else if (usuario.esMod()) {
      auths.add(new SimpleGrantedAuthority("ROLE_MODERADOR"));
    }
    this.authorities = auths;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

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

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

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
