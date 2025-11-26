package com.libratrack.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Representa a un usuario registrado en la plataforma.
 *
 * <p>Gestiona las credenciales de acceso, información de perfil y roles de sistema (Administrador,
 * Moderador).
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "usuarios")
public class Usuario {

  // =============================================================================================
  // IDENTIFICADOR
  // =============================================================================================

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // =============================================================================================
  // CREDENCIALES Y PERFIL
  // =============================================================================================

  @NotBlank(message = "{validation.usuario.username.required}")
  @Size(min = 4, max = 50, message = "{validation.usuario.username.size}")
  @Column(unique = true, nullable = false, length = 50)
  private String username;

  @NotBlank(message = "{validation.usuario.email.required}")
  @Email(message = "{validation.usuario.email.format}")
  @Column(unique = true, nullable = false, length = 100)
  private String email;

  /** Contraseña encriptada (BCrypt). Nunca debe almacenarse en texto plano. */
  @NotBlank(message = "{validation.usuario.password.required}")
  @Column(nullable = false)
  private String password;

  @Column(length = 255)
  private String fotoPerfilUrl;

  // =============================================================================================
  // ROLES Y PERMISOS
  // =============================================================================================

  @Column(nullable = false)
  private Boolean esModerador = false;

  @Column(nullable = false)
  private Boolean esAdministrador = false;

  // =============================================================================================
  // CONSTRUCTORES
  // =============================================================================================

  public Usuario() {}

  public Usuario(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  // =============================================================================================
  // MÉTODOS DE UTILIDAD (NO PERSISTENTES)
  // =============================================================================================

  /** Verifica si el usuario tiene privilegios de administrador. */
  @Transient
  public boolean esAdmin() {
    return Boolean.TRUE.equals(this.esAdministrador);
  }

  /**
   * Verifica si el usuario tiene privilegios de moderación. Un administrador es implícitamente un
   * moderador.
   */
  @Transient
  public boolean esMod() {
    return esAdmin() || Boolean.TRUE.equals(this.esModerador);
  }

  // =============================================================================================
  // GETTERS Y SETTERS
  // =============================================================================================

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Boolean getEsModerador() {
    return esModerador;
  }

  public void setEsModerador(Boolean esModerador) {
    this.esModerador = esModerador;
  }

  public String getFotoPerfilUrl() {
    return fotoPerfilUrl;
  }

  public void setFotoPerfilUrl(String fotoPerfilUrl) {
    this.fotoPerfilUrl = fotoPerfilUrl;
  }

  public Boolean getEsAdministrador() {
    return esAdministrador;
  }

  public void setEsAdministrador(Boolean esAdministrador) {
    this.esAdministrador = esAdministrador;
  }

  // =============================================================================================
  // EQUALS, HASHCODE & TOSTRING
  // =============================================================================================

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Usuario usuario = (Usuario) o;
    return id != null && Objects.equals(id, usuario.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "Usuario{"
        + "id="
        + id
        + ", username='"
        + username
        + '\''
        + ", email='"
        + email
        + '\''
        + ", roles=[Admin:"
        + esAdministrador
        + ", Mod:"
        + esModerador
        + "]"
        + '}';
  }
}
