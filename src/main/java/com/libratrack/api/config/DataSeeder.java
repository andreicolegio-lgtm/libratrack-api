package com.libratrack.api.config;

import com.github.javafaker.Faker;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Genero;
import com.libratrack.api.entity.PropuestaElemento; // <--- Asegúrate de tener esta entidad
import com.libratrack.api.entity.Resena;
import com.libratrack.api.entity.Tipo;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.model.*;
import com.libratrack.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
// Esta línea hace la magia: Solo carga la clase si 'app.seeder.enabled' es 'true'
@ConditionalOnProperty(name = "app.seeder.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ElementoRepository elementoRepository;
    @Autowired private ResenaRepository resenaRepository;
    @Autowired private PropuestaElementoRepository propuestaElementoRepository; // <--- Repositorio de propuestas
    @Autowired private TipoRepository tipoRepository;
    @Autowired private GeneroRepository generoRepository; // <--- Repositorio de géneros
    @Autowired private PasswordEncoder passwordEncoder;

    // Configura Faker en español
    private final Faker faker = new Faker(new Locale.Builder().setLanguage("es").build());
    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Solo ejecutamos si hay pocos datos
        if (elementoRepository.count() > 20) {
            System.out.println(">>> DataSeeder: La base de datos ya tiene datos. Saltando carga.");
            return;
        }

        System.out.println(">>> DataSeeder: Iniciando carga masiva de datos...");

        // 1. Crear USUARIOS (20 usuarios)
        List<Usuario> usuarios = new ArrayList<>();
        Set<String> usernamesUsados = new HashSet<>(); // Para evitar duplicados en memoria

        // Admin fijo
        if (!usuarioRepository.existsByEmail("admin@libratrack.com")) {
            Usuario admin = new Usuario();
            admin.setUsername("AdminDrakand");
            admin.setEmail("admin@libratrack.com");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setEsAdministrador(true);
            admin.setEsModerador(true);
            usuarioRepository.save(admin);
            usuarios.add(admin);
            usernamesUsados.add("AdminDrakand"); // Registrar admin
        } else {
            // Si ya existe (ej: rerun sin limpiar usuarios), cargarlo
            Usuario admin = usuarioRepository.findByUsername("AdminDrakand").orElse(null);
            if (admin != null) {
                usuarios.add(admin);
                usernamesUsados.add("AdminDrakand");
            }
        }

        int usuariosCreados = 0;
        int intentosUsuario = 0;
        
        while (usuariosCreados < 20 && intentosUsuario < 100) {
            intentosUsuario++;
            String nombre = faker.name().username();
            String email = faker.internet().emailAddress();

            // Comprobar BD y Memoria
            if (usuarioRepository.existsByUsername(nombre) || usernamesUsados.contains(nombre)) {
                continue;
            }
            if (usuarioRepository.existsByEmail(email)) {
                continue;
            }

            Usuario u = new Usuario();
            u.setUsername(nombre);
            u.setEmail(email);
            u.setPassword(passwordEncoder.encode("123456"));
            u.setEsModerador(random.nextBoolean());
            
            usuarios.add(u);
            usernamesUsados.add(nombre); // Marcar como usado
            usuariosCreados++;
        }
        
        // Guardamos solo los nuevos (excluyendo al admin si ya estaba guardado)
        // Filtrar aquellos que no tengan ID aún para evitar errores de "detached entity" si el admin venía de BD
        List<Usuario> nuevosUsuarios = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u.getId() == null) { // Solo guardar los que no tienen ID (nuevos)
                nuevosUsuarios.add(u);
            }
        }
        
        if (!nuevosUsuarios.isEmpty()) {
            List<Usuario> guardados = usuarioRepository.saveAll(nuevosUsuarios);
            // Reemplazar en la lista 'usuarios' los objetos sin ID por los guardados con ID
            // para que luego al crear elementos/reseñas tengan ID válido.
            usuarios.addAll(guardados); 
            // Limpiamos la lista original de duplicados o referencias viejas y nos quedamos con la lista final buena
            // Mejor estrategia: Recargar todos los usuarios recién creados + admin
            usuarios = usuarioRepository.findAll(); 
        } else {
             // Si no hubo nuevos, asegurarnos de tener usuarios cargados para el resto del seeder
             usuarios = usuarioRepository.findAll();
        }
        
        System.out.println(">>> Usuarios disponibles: " + usuarios.size());

        // Cargar Tipos y Géneros
        List<Tipo> tiposDisponibles = tipoRepository.findAll();
        List<Genero> generosDisponibles = generoRepository.findAll(); // <--- Ahora lo usamos

        if (tiposDisponibles.isEmpty()) {
            System.out.println(">>> ERROR: No hay Tipos cargados. Revisa tu data.sql");
            return;
        }

        // 2. Crear ELEMENTOS (50 elementos)
        List<Elemento> elementos = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Elemento e = new Elemento();
            e.setTitulo(faker.book().title());
            e.setDescripcion(faker.lorem().paragraph(3));
            
            // Tipo aleatorio
            Tipo tipoRandom = tiposDisponibles.get(random.nextInt(tiposDisponibles.size()));
            e.setTipo(tipoRandom);
            
            // --- NUEVO: Asignar Géneros Aleatorios (1 a 3) ---
            if (!generosDisponibles.isEmpty()) {
                Set<Genero> generosParaEsteElemento = new HashSet<>();
                int numGeneros = 1 + random.nextInt(3); // Entre 1 y 3 géneros
                for (int g = 0; g < numGeneros; g++) {
                    generosParaEsteElemento.add(generosDisponibles.get(random.nextInt(generosDisponibles.size())));
                }
                e.setGeneros(generosParaEsteElemento);
            }
            // -------------------------------------------------

            e.setUrlImagen("https://picsum.photos/seed/" + i + "/300/450");
            e.setEstadoPublicacion(random.nextBoolean() ? EstadoPublicacion.AVAILABLE : EstadoPublicacion.RELEASING);
            e.setEstadoContenido(EstadoContenido.OFICIAL);
            
            // Asignar Creador (Usuario)
            e.setCreador(usuarios.get(random.nextInt(usuarios.size())));
            
            // Datos específicos
            if (tipoRandom.getNombre().equals("Book")) {
                e.setTotalPaginasLibro(faker.number().numberBetween(100, 1000));
            } else if (tipoRandom.getNombre().equals("Anime")) {
                e.setTotalUnidades(faker.number().numberBetween(12, 24));
            }
            
            elementos.add(e);
        }
        elementos = elementoRepository.saveAll(elementos);
        System.out.println(">>> Elementos creados: " + elementos.size());

        // 3. Crear RESEÑAS (100 reseñas únicas)
        List<Resena> resenas = new ArrayList<>();
        Set<String> paresUnicos = new HashSet<>(); // Para evitar duplicados
        int intentos = 0;
        
        // Intentamos generar 100 reseñas, pero con un límite de intentos para no bloquear
        while (resenas.size() < 100 && intentos < 500) {
            intentos++;
            
            Usuario u = usuarios.get(random.nextInt(usuarios.size()));
            Elemento e = elementos.get(random.nextInt(elementos.size()));
            
            // Creamos una clave única "ID_Usuario-ID_Elemento"
            String clave = u.getId() + "-" + e.getId();
            
            // Si ya existe esta combinación, saltamos al siguiente intento
            if (paresUnicos.contains(clave)) {
                continue;
            }
            
            // Si es nueva, la registramos y creamos la reseña
            paresUnicos.add(clave);
            
            Resena r = new Resena();
            r.setUsuario(u);
            r.setElemento(e);
            r.setValoracion(faker.number().numberBetween(1, 6)); // 1 a 5
            r.setTextoResena(faker.lorem().paragraph());
            r.setFechaCreacion(faker.date().past(365, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            resenas.add(r);
        }
        
        resenaRepository.saveAll(resenas);
        System.out.println(">>> Reseñas creadas: " + resenas.size());

        // 4. Crear PROPUESTAS (20 propuestas)
        List<PropuestaElemento> propuestas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            PropuestaElemento p = new PropuestaElemento();
            p.setTituloSugerido(faker.book().title());
            p.setDescripcionSugerida(faker.lorem().paragraph()); // Añadimos descripción por si acaso
            p.setProponente(usuarios.get(random.nextInt(usuarios.size())));
            
            // Tipo sugerido (Nombre)
            p.setTipoSugerido(tiposDisponibles.get(random.nextInt(tiposDisponibles.size())).getNombre());
            
            // --- CORRECCIÓN: Asignar Géneros Sugeridos (String separado por comas) ---
            List<String> nombresGeneros = new ArrayList<>();
            int numGenerosProp = 1 + random.nextInt(2); // 1 o 2 géneros
            for (int k = 0; k < numGenerosProp; k++) {
                nombresGeneros.add(generosDisponibles.get(random.nextInt(generosDisponibles.size())).getNombre());
            }
            p.setGenerosSugeridos(String.join(", ", nombresGeneros));
            // -------------------------------------------------------------------------

            // Estados variados
            int estadoRand = random.nextInt(3);
            if (estadoRand == 0) p.setEstadoPropuesta(EstadoPropuesta.PENDIENTE);
            else if (estadoRand == 1) p.setEstadoPropuesta(EstadoPropuesta.APROBADO);
            else p.setEstadoPropuesta(EstadoPropuesta.RECHAZADO);
            
            p.setUrlImagen("https://picsum.photos/seed/prop" + i + "/300/450");
            propuestas.add(p);
        }
        propuestaElementoRepository.saveAll(propuestas);
        System.out.println(">>> Propuestas creadas: " + propuestas.size());
        
        System.out.println(">>> CARGA DE DATOS COMPLETADA <<<");
    }
}