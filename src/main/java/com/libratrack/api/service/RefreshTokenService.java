package com.libratrack.api.service;

import com.libratrack.api.entity.RefreshToken;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ResourceNotFoundException;
import com.libratrack.api.exception.TokenRefreshException;
import com.libratrack.api.repository.RefreshTokenRepository;
import com.libratrack.api.repository.UsuarioRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para la gestión del ciclo de vida de los tokens de refresco. Incluye creación,
 * validación, eliminación y limpieza automática.
 */
@Service
public class RefreshTokenService {

  private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

  @Value("${libratrack.app.jwtRefreshExpirationMs}")
  private Long refreshTokenDurationMs;

  @Autowired private RefreshTokenRepository refreshTokenRepository;
  @Autowired private UsuarioRepository usuarioRepository;

  @Transactional(readOnly = true)
  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  @Transactional
  public RefreshToken createRefreshToken(String username) {
    Usuario usuario =
        usuarioRepository
            .findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("{exception.user.not_found}"));

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUsuario(usuario);
    refreshToken.setFechaExpiracion(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setToken(UUID.randomUUID().toString());

    return refreshTokenRepository.save(refreshToken);
  }

  /** Verifica si el token ha caducado. Si es así, lo elimina de la BD y lanza excepción. */
  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getFechaExpiracion().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(token.getToken(), "{exception.token.refresh.expired}");
    }
    return token;
  }

  @Transactional
  public void deleteByToken(String token) {
    refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
  }

  /**
   * Tarea programada: Se ejecuta automáticamente todos los días a las 4:00 AM. Elimina físicamente
   * de la base de datos los tokens que ya han expirado para liberar espacio.
   */
  @Transactional
  @Scheduled(cron = "0 0 4 * * ?")
  public void purgeExpiredTokens() {
    Instant now = Instant.now();
    logger.info("Iniciando purga de tokens expirados antes de: {}", now);
    refreshTokenRepository.deleteByFechaExpiracionBefore(now);
    logger.info("Purga de tokens finalizada.");
  }
}
