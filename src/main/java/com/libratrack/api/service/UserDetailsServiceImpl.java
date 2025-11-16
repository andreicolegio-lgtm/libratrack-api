package com.libratrack.api.service;

import com.libratrack.api.entity.Usuario;
import com.libratrack.api.repository.UsuarioRepository;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired private UsuarioRepository usuarioRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    Usuario usuario =
        usuarioRepository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "Usuario no encontrado con username: " + username));

    Set<GrantedAuthority> authorities = new HashSet<>();

    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

    if (usuario.esAdmin()) {
      authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
      authorities.add(new SimpleGrantedAuthority("ROLE_MODERADOR"));
    } else if (usuario.esMod()) {
      authorities.add(new SimpleGrantedAuthority("ROLE_MODERADOR"));
    }

    return new User(usuario.getUsername(), usuario.getPassword(), authorities);
  }
}
