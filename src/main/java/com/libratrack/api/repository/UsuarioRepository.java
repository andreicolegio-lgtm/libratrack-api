package com.libratrack.api.repository;

import com.libratrack.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
// --- ¡NUEVA IMPORTACIÓN! ---
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * --- ¡ACTUALIZADO (Sprint 7)! ---
 * Ahora extiende JpaSpecificationExecutor para permitir consultas dinámicas
 * (búsqueda y filtrado en el panel de admin).
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    // (RF02) Usado por AuthController
    Optional<Usuario> findByEmail(String email);

    // (RF01, RF04) Usado por UserDetailsServiceImpl y UsuarioService
    Optional<Usuario> findByUsername(String username);

    // (RF01) Usado por UsuarioService (register)
    Boolean existsByEmail(String email);

    // (RF01) Usado por UsuarioService (register)
    Boolean existsByUsername(String username);
}