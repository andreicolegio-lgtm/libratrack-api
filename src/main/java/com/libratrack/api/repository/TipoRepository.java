package com.libratrack.api.repository;

import com.libratrack.api.entity.Tipo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/** Repositorio para la gestión de {@link Tipo} (categorías principales de contenido). */
@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long> {

  /**
   * Busca un tipo por su nombre exacto.
   *
   * @param nombre Nombre del tipo (ej. "Anime").
   * @return Optional con el tipo encontrado.
   */
  Optional<Tipo> findByNombre(String nombre);

  /**
   * Recupera todos los tipos disponibles, cargando también sus géneros permitidos.
   *
   * <p>Utiliza {@code LEFT JOIN FETCH} para optimizar la carga de la colección {@code
   * generosPermitidos} en una sola consulta SQL, evitando múltiples accesos a la base de datos.
   *
   * @return Lista de tipos con sus géneros inicializados.
   */
  @Query("SELECT DISTINCT t FROM Tipo t LEFT JOIN FETCH t.generosPermitidos")
  List<Tipo> findAllWithGeneros();
}
