package com.libratrack.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

/**
 * Representa un token de refresco (Refresh Token) utilizado en la autenticación JWT.
 *
 * <p>Este token tiene un tiempo de vida más largo que el Access Token y se utiliza para solicitar
 * nuevos Access Tokens sin requerir que el usuario introduzca sus credenciales nuevamente. Es vital
 * para la experiencia de usuario de "mantener sesión iniciada".
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

  // =============================================================================================
  // IDENTIFICADOR
  // =============================================================================================

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // =============================================================================================
  // RELACIONES
  // =============================================================================================

  /**
   * Usuario al que pertenece este token. La relación es Lazy para no cargar el usuario completo
   * cada vez que validamos el token.
   */
  @NotNull(message = "{validation.refreshtoken.usuario.required}")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  // =============================================================================================
  // DATOS DEL TOKEN
  // =============================================================================================

  /**
   * El token opaco (generalmente un UUID) que se envía al cliente. Debe ser único en todo el
   * sistema.
   */
  @NotBlank(message = "{validation.refreshtoken.token.required}")
  @Column(nullable = false, unique = true)
  private String token;

  /** Fecha y hora exacta en la que este token dejará de ser válido. */
  @NotNull(message = "{validation.refreshtoken.expiracion.required}")
  @Column(nullable = false)
  private Instant fechaExpiracion;

  // =============================================================================================
  // CONSTRUCTORES
  // =============================================================================================

  public RefreshToken() {}

  // =============================================================================================
  // GETTERS Y SETTERS
  // =============================================================================================

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Instant getFechaExpiracion() {
    return fechaExpiracion;
  }

  public void setFechaExpiracion(Instant fechaExpiracion) {
    this.fechaExpiracion = fechaExpiracion;
  }

  // =============================================================================================
  // EQUALS, HASHCODE & TOSTRING
  // =============================================================================================

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RefreshToken that = (RefreshToken) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "RefreshToken{"
        + "id="
        + id
        + ", token='"
        + token
        + '\''
        + ", fechaExpiracion="
        + fechaExpiracion
        + '}';
  }
}
