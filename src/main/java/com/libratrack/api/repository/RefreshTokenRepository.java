package com.libratrack.api.repository;

import com.libratrack.api.entity.RefreshToken;
import com.libratrack.api.entity.Usuario;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Repositorio para la gestión del ciclo de vida de los tokens de refresco JWT. */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  /**
   * Busca un refresh token por su cadena única. Utiliza JOIN FETCH para cargar el usuario asociado,
   * necesario para generar el nuevo Access Token.
   */
  Optional<RefreshToken> findByToken(String token);

  /**
   * Elimina todos los tokens cuya fecha de expiración sea anterior al instante dado. Se utiliza en
   * tareas programadas (@Scheduled) para limpiar la base de datos.
   */
  @Modifying
  @Transactional
  void deleteByFechaExpiracionBefore(Instant now);

  /**
   * Elimina todos los tokens asociados a un usuario específico. Útil para "Cerrar sesión en todos
   * los dispositivos".
   */
  @Modifying
  @Transactional
  void deleteByUsuario(Usuario usuario);
}
