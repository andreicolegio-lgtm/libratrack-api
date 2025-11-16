package com.libratrack.api.repository;

import com.libratrack.api.entity.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository
    extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

  Optional<Usuario> findByEmail(String email);

  Optional<Usuario> findByUsername(String username);

  Boolean existsByEmail(String email);

  Boolean existsByUsername(String username);
}
