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

        // --- INICIO DE LÍNEAS DE DEBUGGEO ---
        System.out.println("\n--- [DEBUG DE ROLES] ---");
        System.out.println(">>> Usuario encontrado: " + usuario.getUsername());
        System.out.println(">>> Valor de 'esModerador' (leído por Java): " + usuario.getEsModerador());
        // --- FIN DE LÍNEAS DE DEBUGGEO ---

        // 2. Definimos los "roles" o "permisos" de este usuario (RF03)
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // Asignamos el rol básico
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        // Si nuestro booleano 'esModerador' es true, añadimos el rol de Moderador
        if (usuario.getEsModerador() != null && usuario.getEsModerador()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_MODERADOR"));
            // --- LÍNEA DE DEBUGGEO 2 ---
            System.out.println(">>> ¡Rol MODERADOR añadido!");
            // --- FIN DE LÍNEA DE DEBUGGEO 2 ---
        }
        
        // --- LÍNEA DE DEBUGGEO 3 ---
        System.out.println(">>> Roles finales asignados: " + authorities.toString());
        System.out.println("--- [FIN DEBUG DE ROLES] ---\n");
        // --- FIN DE LÍNEA DE DEBUGGEO 3 ---

        // 3. Devolvemos el objeto UserDetails que Spring entiende
        return new User(
            usuario.getUsername(),
            usuario.getPassword(), // La contraseña ya cifrada de la BD
            authorities // La lista de roles
        );
    }
}