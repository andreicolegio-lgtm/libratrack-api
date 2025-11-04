package com.libratrack.api.repository;

import com.libratrack.api.entity.Genero;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneroRepository extends JpaRepository<Genero, Long> {
    
    // Método "mágico" para buscar un Genero por su nombre
    Optional<Genero> findByNombre(String nombre);
    
}