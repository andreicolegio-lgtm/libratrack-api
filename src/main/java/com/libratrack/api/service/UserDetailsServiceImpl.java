package com.libratrack.api.service;

import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ResourceNotFoundException;
import com.libratrack.api.repository.UsuarioRepository;
import com.libratrack.api.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired private UsuarioRepository usuarioRepository;

  // Método usado por el filtro JWT (autenticación por Token)
  public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
    Usuario usuario =
        usuarioRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("INVALID_USER_TOKEN"));

    return new CustomUserDetails(usuario);
  }

  // Método usado por el Login estándar (autenticación por credenciales)
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuario usuario =
        usuarioRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("E_INVALID_CREDENTIALS"));

    return new CustomUserDetails(usuario);
  }
}
