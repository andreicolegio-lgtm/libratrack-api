package com.libratrack.api.repository;

import com.libratrack.api.entity.Genero;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repositorio para la gestión de {@link Genero}. */
@Repository
public interface GeneroRepository extends JpaRepository<Genero, Long> {

  /**
   * Busca un género por su nombre exacto.
   *
   * @param nombre Nombre del género (ej. "Acción").
   * @return Optional conteniendo el género si existe.
   */
  Optional<Genero> findByNombre(String nombre);
}
