package com.libratrack.api.service;

import com.libratrack.api.entity.Usuario;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service // Marca esto como un servicio de Spring
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Este método es el único que Spring Security necesita.
     * Se llama automáticamente cuando se valida un token.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Buscamos al usuario en nuestro repositorio
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        // 2. Definimos los "roles" o "permisos" de este usuario (RF03)
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // Asignamos el rol básico
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        // Si nuestro booleano 'esModerador' es true, añadimos el rol de Moderador
        if (usuario.getEsModerador() != null && usuario.getEsModerador()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_MODERADOR"));
        }
        
        // (En el futuro, podríamos añadir "ROLE_ADMINISTRADOR" aquí)

        // 3. Devolvemos el objeto UserDetails que Spring entiende
        return new User(
            usuario.getUsername(),
            usuario.getPassword(), // La contraseña ya cifrada de la BD
            authorities // La lista de roles
        );
    }
}