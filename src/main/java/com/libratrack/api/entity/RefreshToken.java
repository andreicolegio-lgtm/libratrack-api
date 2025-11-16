package com.libratrack.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id", nullable = false)
  @NotNull
  private Usuario usuario;

  @Column(nullable = false, unique = true)
  @NotBlank
  private String token;

  @Column(nullable = false)
  @NotNull
  private Instant fechaExpiracion;

  public RefreshToken() {}

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
}
