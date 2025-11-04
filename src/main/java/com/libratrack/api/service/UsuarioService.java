package com.libratrack.api.service;

import com.libratrack.api.entity.Usuario; // Importa tu Entidad
import com.libratrack.api.repository.UsuarioRepository; // Importa tu Repositorio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// (Importaremos la seguridad más adelante cuando la configuremos)
// import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service // Marca esta clase como un "Servicio" (el cerebro de la lógica)
public class UsuarioService {

    // --- INYECCIÓN DE DEPENDENCIAS ---
    // En lugar de crear un "new UsuarioRepository()", le pedimos
    // a Spring que nos "inyecte" (nos pase) el objeto que él ya
    // tiene en memoria. Esto es una buena práctica (Inversión de Control).

    @Autowired // Pide a Spring que inyecte el Repositorio aquí
    private UsuarioRepository usuarioRepository;

    // (Descomentaremos esto cuando configuremos la seguridad)
    // @Autowired
    // private PasswordEncoder passwordEncoder;


    // --- MÉTODOS DE LÓGICA DE NEGOCIO ---

    /**
     * Lógica para registrar un nuevo usuario (RF01).
     *
     * @param nuevoUsuario El objeto Usuario con los datos del registro.
     * @return El Usuario guardado.
     * @throws Exception Si el email o el username ya existen.
     */
    public Usuario registrarUsuario(Usuario nuevoUsuario) throws Exception {
        // 1. Comprobar si el email ya existe (usando el método mágico del repo)
        if (usuarioRepository.existsByEmail(nuevoUsuario.getEmail())) {
            throw new Exception("El email ya está registrado");
        }

        // 2. Comprobar si el nombre de usuario ya existe
        if (usuarioRepository.existsByUsername(nuevoUsuario.getUsername())) {
            throw new Exception("El nombre de usuario ya existe");
        }

        // 3. Cifrar la contraseña (¡IMPORTANTE!)
        // Por ahora, lo guardamos en texto plano. Lo cambiaremos al configurar Spring Security.
        // String passCifrada = passwordEncoder.encode(nuevoUsuario.getPassword());
        // nuevoUsuario.setPassword(passCifrada);
        
        // (Línea temporal hasta que configuremos seguridad)
        // No hagas esto en producción, pero vale para probar:
        nuevoUsuario.setPassword(nuevoUsuario.getPassword()); // Guardado temporal en texto plano

        // 4. Guardar el usuario en la base de datos
        return usuarioRepository.save(nuevoUsuario);
    }

    /**
     * Lógica para el login (RF02).
     *
     * @param username El nombre de usuario.
     * @param password La contraseña en texto plano.
     * @return El Usuario si el login es correcto.
     * @throws Exception Si el usuario no existe o la contraseña es incorrecta.
     */
    public Usuario login(String username, String password) throws Exception {
        // 1. Buscar al usuario por su nombre de usuario
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isEmpty()) {
            // El usuario no existe
            throw new Exception("Usuario o contraseña incorrectos");
        }

        Usuario usuario = usuarioOpt.get();

        // 2. Comprobar la contraseña
        // (Línea temporal hasta que configuremos seguridad)
        // La comprobación real sería:
        // if (passwordEncoder.matches(password, usuario.getPassword())) { ... }
        
        if (usuario.getPassword().equals(password)) {
            // ¡Login correcto!
            return usuario;
        } else {
            // Contraseña incorrecta
            throw new Exception("Usuario o contraseña incorrectos");
        }
    }
}