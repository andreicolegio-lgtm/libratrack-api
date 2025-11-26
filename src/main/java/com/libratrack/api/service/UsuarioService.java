package com.libratrack.api.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.libratrack.api.dto.PasswordChangeDTO;
import com.libratrack.api.dto.RolUpdateDTO;
import com.libratrack.api.dto.UsuarioResponseDTO;
import com.libratrack.api.dto.UsuarioUpdateDTO;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ConflictException;
import com.libratrack.api.exception.ResourceNotFoundException;
import com.libratrack.api.repository.UsuarioRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio principal para la gestión de la lógica de negocio relacionada con usuarios. Maneja
 * perfiles, registro, autenticación con Google, cambios de contraseña y administración.
 */
@Service
public class UsuarioService {

  private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

  // Regex: Al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial.
  private static final String PASSWORD_REGEX =
      "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

  @Autowired private UsuarioRepository usuarioRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private MessageSource messageSource;

  // =============================================================================================
  // GESTIÓN DE PERFIL DE USUARIO (MI CUENTA)
  // =============================================================================================

  @Transactional(readOnly = true)
  public UsuarioResponseDTO getMiPerfilById(Long userId) {
    Usuario usuario = findUserByIdOrThrow(userId);
    return new UsuarioResponseDTO(usuario);
  }

  @Transactional
  public UsuarioResponseDTO updateMiPerfilById(Long userId, UsuarioUpdateDTO updateDto) {
    Usuario usuarioActual = findUserByIdOrThrow(userId);
    String nuevoUsername = updateDto.getUsername().trim();

    // Si el username ha cambiado, verificar disponibilidad
    if (!usuarioActual.getUsername().equals(nuevoUsername)) {
      if (usuarioRepository.existsByUsername(nuevoUsername)) {
        throw new ConflictException("{exception.user.username.exists}");
      }
      usuarioActual.setUsername(nuevoUsername);
    }

    Usuario usuarioActualizado = usuarioRepository.save(usuarioActual);
    return new UsuarioResponseDTO(usuarioActualizado);
  }

  @Transactional
  public UsuarioResponseDTO updateFotoPerfilById(Long userId, String fotoUrl) {
    Usuario usuario = findUserByIdOrThrow(userId);
    usuario.setFotoPerfilUrl(fotoUrl);
    Usuario usuarioActualizado = usuarioRepository.save(usuario);
    return new UsuarioResponseDTO(usuarioActualizado);
  }

  @Transactional
  public ResponseEntity<Map<String, String>> changePasswordById(
      Long userId, PasswordChangeDTO passwordDto) {
    Usuario usuarioActual = findUserByIdOrThrow(userId);

    // 1. Validar contraseña actual
    String currentPassword = passwordDto.getContraseñaActual();
    if (!passwordEncoder.matches(currentPassword, usuarioActual.getPassword())) {
      logger.warn("Intento fallido de cambio de contraseña para usuario ID: {}", userId);
      throw new ConflictException("{exception.password.incorrect}");
    }

    // 2. Validar nueva contraseña (no debe ser igual a la anterior)
    String newPassword = passwordDto.getNuevaContraseña();
    if (passwordEncoder.matches(newPassword, usuarioActual.getPassword())) {
      throw new ConflictException("{exception.password.unchanged}");
    }

    // 3. Validar complejidad
    validatePasswordComplexity(newPassword);

    // 4. Actualizar
    usuarioActual.setPassword(passwordEncoder.encode(newPassword));
    usuarioRepository.save(usuarioActual);

    logger.info("Contraseña actualizada exitosamente para usuario ID: {}", userId);

    String successMessage =
        messageSource.getMessage("message.password.updated", null, LocaleContextHolder.getLocale());
    return ResponseEntity.ok(Map.of("message", successMessage));
  }

  // =============================================================================================
  // REGISTRO Y AUTENTICACIÓN
  // =============================================================================================

  @Transactional
  public UsuarioResponseDTO registrarUsuario(Usuario nuevoUsuario) {
    if (usuarioRepository.existsByUsername(nuevoUsuario.getUsername())) {
      throw new ConflictException("{exception.user.username.exists}");
    }
    if (usuarioRepository.existsByEmail(nuevoUsuario.getEmail())) {
      throw new ConflictException("{exception.user.email.exists}");
    }

    // Encriptar contraseña y asignar roles por defecto
    nuevoUsuario.setPassword(passwordEncoder.encode(nuevoUsuario.getPassword()));
    nuevoUsuario.setEsModerador(false);
    nuevoUsuario.setEsAdministrador(false);

    Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
    logger.info("Nuevo usuario registrado: {}", usuarioGuardado.getUsername());

    return new UsuarioResponseDTO(usuarioGuardado);
  }

  @Transactional
  public Usuario findOrCreateGoogleUser(Payload payload) {
    String email = payload.getEmail();
    Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

    if (usuarioOpt.isPresent()) {
      // Usuario existente: actualizar foto si viene de Google
      Usuario usuario = usuarioOpt.get();
      if (payload.get("picture") != null) {
        usuario.setFotoPerfilUrl((String) payload.get("picture"));
        return usuarioRepository.save(usuario);
      }
      return usuario;
    } else {
      // Nuevo usuario vía Google
      Usuario nuevoUsuario = new Usuario();
      nuevoUsuario.setEmail(email);
      nuevoUsuario.setUsername(generarUsernameUnicoDesdeGoogle(payload));

      // Contraseña aleatoria (el usuario entra con Google, no la usa, pero es requerida por BD)
      nuevoUsuario.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

      if (payload.get("picture") != null) {
        nuevoUsuario.setFotoPerfilUrl((String) payload.get("picture"));
      }

      nuevoUsuario.setEsModerador(false);
      nuevoUsuario.setEsAdministrador(false);

      logger.info("Nuevo usuario creado vía Google: {}", email);
      return usuarioRepository.save(nuevoUsuario);
    }
  }

  // =============================================================================================
  // ADMINISTRACIÓN
  // =============================================================================================

  @Transactional(readOnly = true)
  public Page<UsuarioResponseDTO> getAllUsuarios(
      Pageable pageable, String search, String roleFilter) {
    Specification<Usuario> spec =
        (root, query, cb) -> {
          List<Predicate> predicates = new ArrayList<>();

          if (search != null && !search.isBlank()) {
            String likePattern = "%" + search.toLowerCase() + "%";
            predicates.add(
                cb.or(
                    cb.like(cb.lower(root.get("username")), likePattern),
                    cb.like(cb.lower(root.get("email")), likePattern)));
          }

          if (roleFilter != null && !roleFilter.isBlank()) {
            if ("MODERADOR".equalsIgnoreCase(roleFilter)) {
              predicates.add(cb.isTrue(root.get("esModerador")));
            } else if ("ADMIN".equalsIgnoreCase(roleFilter)) {
              predicates.add(cb.isTrue(root.get("esAdministrador")));
            }
          }

          return cb.and(predicates.toArray(new Predicate[0]));
        };

    return usuarioRepository.findAll(spec, pageable).map(UsuarioResponseDTO::new);
  }

  @Transactional
  public UsuarioResponseDTO updateUserRoles(Long usuarioId, RolUpdateDTO dto) {
    Usuario usuario = findUserByIdOrThrow(usuarioId);
    usuario.setEsModerador(dto.getEsModerador());
    usuario.setEsAdministrador(dto.getEsAdministrador());

    Usuario usuarioGuardado = usuarioRepository.save(usuario);
    logger.info(
        "Roles actualizados para usuario ID {}: Admin={}, Mod={}",
        usuarioId,
        usuarioGuardado.getEsAdministrador(),
        usuarioGuardado.getEsModerador());

    return new UsuarioResponseDTO(usuarioGuardado);
  }

  // =============================================================================================
  // MÉTODOS AUXILIARES PRIVADOS
  // =============================================================================================

  private Usuario findUserByIdOrThrow(Long userId) {
    return usuarioRepository
        .findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("{exception.user.not_found}"));
  }

  private void validatePasswordComplexity(String password) {
    if (!password.matches(PASSWORD_REGEX)) {
      throw new IllegalArgumentException("{validation.password.complexity}");
    }
  }

  private String generarUsernameUnicoDesdeGoogle(Payload payload) {
    String baseUsername = (String) payload.get("given_name");
    if (baseUsername == null || baseUsername.isBlank()) {
      baseUsername = payload.getEmail().split("@")[0];
    }
    // Limpieza básica
    baseUsername = baseUsername.replaceAll("[^a-zA-Z0-9]", "");

    if (baseUsername.length() > 40) {
      baseUsername = baseUsername.substring(0, 40);
    }

    String username = baseUsername;
    int i = 1;
    while (usuarioRepository.existsByUsername(username)) {
      String suffix = String.valueOf(i);
      int maxBaseLen = 50 - suffix.length();
      if (baseUsername.length() > maxBaseLen) {
        username = baseUsername.substring(0, maxBaseLen) + suffix;
      } else {
        username = baseUsername + suffix;
      }
      i++;
    }
    return username;
  }
}
