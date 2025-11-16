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

@Service
public class RefreshTokenService {

  private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

  @Value("${libratrack.app.jwtRefreshExpirationMs}")
  private Long refreshTokenDurationMs;

  @Autowired private RefreshTokenRepository refreshTokenRepository;

  @Autowired private UsuarioRepository usuarioRepository;

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  @Transactional
  public RefreshToken createRefreshToken(String username) {
    Usuario usuario =
        usuarioRepository
            .findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

    RefreshToken refreshToken = new RefreshToken();

    refreshToken.setUsuario(usuario);
    refreshToken.setFechaExpiracion(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setToken(UUID.randomUUID().toString());

    refreshToken = refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }

  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getFechaExpiracion().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(
          token.getToken(), "Token de refresco caducado. Por favor, inicie sesión de nuevo.");
    }
    return token;
  }

  @Transactional
  public void deleteByToken(String token) {
    refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
  }

  @Transactional
  @Scheduled(cron = "0 0 4 * * ?")
  public void purgeExpiredTokens() {
    Instant now = Instant.now();
    logger.info(
        "Ejecutando tarea de limpieza de tokens de refresco caducados (anteriores a {})...", now);

    refreshTokenRepository.deleteByFechaExpiracionBefore(now);

    logger.info("Tarea de limpieza de tokens completada.");
  }
}
