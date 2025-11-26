package com.libratrack.api.repository;

import com.libratrack.api.entity.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de usuarios.
 *
 * <p>Extiende {@link JpaSpecificationExecutor} para permitir búsquedas dinámicas complejas
 * (filtrado por rol, búsqueda por texto, etc.) utilizadas en paneles de administración.
 */
@Repository
public interface UsuarioRepository
    extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

  /** Busca un usuario por su correo electrónico exacto. */
  Optional<Usuario> findByEmail(String email);

  /**
   * Busca un usuario por su nombre de usuario exacto. Utilizado principalmente en el proceso de
   * autenticación (Login).
   */
  Optional<Usuario> findByUsername(String username);

  /**
   * Verifica si existe algún usuario con el email dado. Útil para validaciones de registro (evitar
   * duplicados).
   */
  Boolean existsByEmail(String email);

  /**
   * Verifica si existe algún usuario con el username dado. Útil para validaciones de registro o
   * cambio de nombre.
   */
  Boolean existsByUsername(String username);
}
