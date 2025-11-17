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
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {
  @Transactional(readOnly = true)
  public UsuarioResponseDTO getMiPerfilById(Long userId) {
    Usuario usuario =
        usuarioRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("INVALID_USER_TOKEN"));
    return new UsuarioResponseDTO(usuario);
  }

  public UsuarioResponseDTO updateMiPerfilById(Long userId, UsuarioUpdateDTO updateDto) {
    Usuario usuarioActual =
        usuarioRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("INVALID_USER_TOKEN"));
    String nuevoUsername = updateDto.getUsername().trim();
    if (usuarioActual.getUsername().equals(nuevoUsername)) {
      return new UsuarioResponseDTO(usuarioActual);
    }
    Optional<Usuario> usuarioExistente = usuarioRepository.findByUsername(nuevoUsername);
    if (usuarioExistente.isPresent()) {
      throw new ConflictException("USERNAME_ALREADY_EXISTS");
    }
    usuarioActual.setUsername(nuevoUsername);
    Usuario usuarioActualizado = usuarioRepository.save(usuarioActual);
    return new UsuarioResponseDTO(usuarioActualizado);
  }

  public void changePasswordById(Long userId, PasswordChangeDTO passwordDto) {
    Usuario usuarioActual =
        usuarioRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("INVALID_USER_TOKEN"));
    String contraseñaActualPlana = passwordDto.getContraseñaActual();
    String contraseñaActualHasheada = usuarioActual.getPassword();
    if (!passwordEncoder.matches(contraseñaActualPlana, contraseñaActualHasheada)) {
      throw new ConflictException("PASSWORD_INCORRECT");
    }
    String nuevaContraseñaPlana = passwordDto.getNuevaContraseña();
    String nuevaContraseñaHasheada = passwordEncoder.encode(nuevaContraseñaPlana);
    usuarioActual.setPassword(nuevaContraseñaHasheada);
    usuarioRepository.save(usuarioActual);
  }

  public UsuarioResponseDTO updateFotoPerfilById(Long userId, String fotoUrl) {
    Usuario usuario =
        usuarioRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("INVALID_USER_TOKEN"));
    usuario.setFotoPerfilUrl(fotoUrl);
    Usuario usuarioActualizado = usuarioRepository.save(usuario);
    return new UsuarioResponseDTO(usuarioActualizado);
  }

  @Autowired private UsuarioRepository usuarioRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Transactional
  public UsuarioResponseDTO registrarUsuario(Usuario nuevoUsuario) {
    if (usuarioRepository.existsByUsername(nuevoUsuario.getUsername())) {
      throw new ConflictException("USERNAME_ALREADY_EXISTS");
    }
    if (usuarioRepository.existsByEmail(nuevoUsuario.getEmail())) {
      throw new ConflictException("EMAIL_ALREADY_REGISTERED");
    }
    String passCifrada = passwordEncoder.encode(nuevoUsuario.getPassword());
    nuevoUsuario.setPassword(passCifrada);
    nuevoUsuario.setEsModerador(false);
    nuevoUsuario.setEsAdministrador(false);
    Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
    return new UsuarioResponseDTO(usuarioGuardado);
  }

  @Transactional(readOnly = true)
  public UsuarioResponseDTO getMiPerfil(String username) {
    Usuario usuario =
        usuarioRepository
            .findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("INVALID_USER_TOKEN"));
    return new UsuarioResponseDTO(usuario);
  }

  @Transactional
  public UsuarioResponseDTO updateMiPerfil(String usernameActual, UsuarioUpdateDTO updateDto) {
    Usuario usuarioActual =
        usuarioRepository
            .findByUsername(usernameActual)
            .orElseThrow(() -> new ResourceNotFoundException("INVALID_USER_TOKEN"));
    String nuevoUsername = updateDto.getUsername().trim();
    if (usuarioActual.getUsername().equals(nuevoUsername)) {
      return new UsuarioResponseDTO(usuarioActual);
    }
    Optional<Usuario> usuarioExistente = usuarioRepository.findByUsername(nuevoUsername);
    if (usuarioExistente.isPresent()) {
      throw new ConflictException("USERNAME_ALREADY_EXISTS");
    }
    usuarioActual.setUsername(nuevoUsername);
    Usuario usuarioActualizado = usuarioRepository.save(usuarioActual);
    return new UsuarioResponseDTO(usuarioActualizado);
  }

  @Transactional
  public void changePassword(String usernameActual, PasswordChangeDTO passwordDto) {
    Usuario usuarioActual =
        usuarioRepository
            .findByUsername(usernameActual)
            .orElseThrow(() -> new ResourceNotFoundException("INVALID_USER_TOKEN"));
    String contraseñaActualPlana = passwordDto.getContraseñaActual();
    String contraseñaActualHasheada = usuarioActual.getPassword();
    if (!passwordEncoder.matches(contraseñaActualPlana, contraseñaActualHasheada)) {
      throw new ConflictException("PASSWORD_INCORRECT");
    }
    String nuevaContraseñaPlana = passwordDto.getNuevaContraseña();
    String nuevaContraseñaHasheada = passwordEncoder.encode(nuevaContraseñaPlana);
    usuarioActual.setPassword(nuevaContraseñaHasheada);
    usuarioRepository.save(usuarioActual);
  }

  @Transactional
  public UsuarioResponseDTO updateFotoPerfil(String username, String fotoUrl) {
    Usuario usuario =
        usuarioRepository
            .findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("INVALID_USER_TOKEN"));
    usuario.setFotoPerfilUrl(fotoUrl);
    Usuario usuarioActualizado = usuarioRepository.save(usuario);
    return new UsuarioResponseDTO(usuarioActualizado);
  }

  @Transactional(readOnly = true)
  public Page<UsuarioResponseDTO> getAllUsuarios(
      Pageable pageable, String search, String roleFilter) {

    Specification<Usuario> spec =
        (root, query, cb) -> {
          List<Predicate> predicates = new ArrayList<>();

          if (search != null && !search.isBlank()) {
            String likePattern = "%" + search.toLowerCase() + "%";
            Predicate searchUsername = cb.like(cb.lower(root.get("username")), likePattern);
            Predicate searchEmail = cb.like(cb.lower(root.get("email")), likePattern);
            predicates.add(cb.or(searchUsername, searchEmail));
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

    Page<Usuario> usuarios = usuarioRepository.findAll(spec, pageable);

    return usuarios.map(UsuarioResponseDTO::new);
  }

  @Transactional
  public UsuarioResponseDTO updateUserRoles(Long usuarioId, RolUpdateDTO dto) {
    Usuario usuario =
        usuarioRepository
            .findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("INVALID_USER_TOKEN"));
    usuario.setEsModerador(dto.getEsModerador());
    usuario.setEsAdministrador(dto.getEsAdministrador());
    Usuario usuarioActualizado = usuarioRepository.save(usuario);
    return new UsuarioResponseDTO(usuarioActualizado);
  }

  @Transactional
  public Usuario findOrCreateGoogleUser(Payload payload) {
    String email = payload.getEmail();
    Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

    if (usuarioOpt.isPresent()) {
      Usuario usuario = usuarioOpt.get();

      if (payload.get("picture") != null) {
        usuario.setFotoPerfilUrl((String) payload.get("picture"));
      }
      return usuarioRepository.save(usuario);

    } else {
      Usuario nuevoUsuario = new Usuario();
      nuevoUsuario.setEmail(email);

      String username = (String) payload.get("given_name");
      if (username == null || username.isBlank()) {
        username = email.split("@")[0];
      }
      if (username.length() > 40) {
        username = username.substring(0, 40);
      }
      nuevoUsuario.setUsername(generarUsernameUnico(username));

      String randomPassword = UUID.randomUUID().toString();
      nuevoUsuario.setPassword(passwordEncoder.encode(randomPassword));

      if (payload.get("picture") != null) {
        nuevoUsuario.setFotoPerfilUrl((String) payload.get("picture"));
      }

      nuevoUsuario.setEsModerador(false);
      nuevoUsuario.setEsAdministrador(false);

      return usuarioRepository.save(nuevoUsuario);
    }
  }

  private String generarUsernameUnico(String baseUsername) {
    String username = baseUsername;
    int i = 1;
    while (usuarioRepository.existsByUsername(username)) {
      String suffix = String.valueOf(i);
      if (baseUsername.length() + suffix.length() > 50) {
        username = baseUsername.substring(0, 50 - suffix.length()) + suffix;
      } else {
        username = baseUsername + suffix;
      }
      i++;
    }
    return username;
  }
}
