package com.libratrack.api.repository;

import com.libratrack.api.entity.Tipo;
import java.util.Optional; // Import para Optional
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long> {
    
    // Método "mágico" para buscar un Tipo por su nombre
    Optional<Tipo> findByNombre(String nombre);
}