package com.libratrack.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.libratrack.api.dto.PasswordChangeDTO;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ConflictException;
import com.libratrack.api.repository.UsuarioRepository;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;

class UsuarioServiceTest {

  @Mock private UsuarioRepository usuarioRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private MessageSource messageSource;

  @InjectMocks private UsuarioService usuarioService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
        .thenReturn("Mocked Message");
  }

  @Test
  void testChangePasswordById_ValidPassword() {
    Long userId = 1L;
    String oldPassword = "OldPassword123!";
    String newPassword = "NewPassword123!";

    Usuario usuario = new Usuario();
    usuario.setId(userId);
    usuario.setPassword("encodedOldPassword");

    PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
    passwordChangeDTO.setContraseñaActual(oldPassword);
    passwordChangeDTO.setNuevaContraseña(newPassword);

    when(usuarioRepository.findById(userId)).thenReturn(java.util.Optional.of(usuario));
    when(passwordEncoder.matches(oldPassword, usuario.getPassword())).thenReturn(true);
    when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

    assertDoesNotThrow(() -> usuarioService.changePasswordById(userId, passwordChangeDTO));

    verify(usuarioRepository).save(usuario);
    assertEquals("encodedNewPassword", usuario.getPassword());
  }

  @Test
  void testChangePasswordById_InvalidPassword() {
    Long userId = 1L;
    String oldPassword = "OldPassword123!";
    String newPassword = "weak";

    Usuario usuario = new Usuario();
    usuario.setId(userId);
    usuario.setPassword("encodedOldPassword");

    PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
    passwordChangeDTO.setContraseñaActual(oldPassword);
    passwordChangeDTO.setNuevaContraseña(newPassword);

    when(usuarioRepository.findById(userId)).thenReturn(java.util.Optional.of(usuario));
    when(passwordEncoder.matches(oldPassword, usuario.getPassword())).thenReturn(true);

    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.changePasswordById(userId, passwordChangeDTO));

    assertEquals("VALIDATION_PASSWORD_COMPLEXITY", exception.getMessage());
    verify(usuarioRepository, never()).save(usuario);
  }

  @Test
  void testChangePasswordById_IncorrectCurrentPassword() {
    Long userId = 1L;
    String oldPassword = "WrongPassword123!";
    String newPassword = "NewPassword123!";

    Usuario usuario = new Usuario();
    usuario.setId(userId);
    usuario.setPassword("encodedOldPassword");

    PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
    passwordChangeDTO.setContraseñaActual(oldPassword);
    passwordChangeDTO.setNuevaContraseña(newPassword);

    when(usuarioRepository.findById(userId)).thenReturn(java.util.Optional.of(usuario));
    when(passwordEncoder.matches(oldPassword, usuario.getPassword())).thenReturn(false);

    Exception exception =
        assertThrows(
            ConflictException.class,
            () -> usuarioService.changePasswordById(userId, passwordChangeDTO));

    assertEquals("PASSWORD_INCORRECT", exception.getMessage());
    verify(usuarioRepository, never()).save(usuario);
  }
}
