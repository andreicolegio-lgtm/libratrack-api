package com.libratrack.api.repository;

import com.libratrack.api.entity.Resena;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Repositorio para gestionar las opiniones y valoraciones de los usuarios. */
@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

  /**
   * Recupera todas las reseñas de un elemento específico, ordenadas de más reciente a más antigua.
   *
   * <p>Incluye un {@code JOIN FETCH} para obtener los datos del autor (username, avatar) en la
   * misma consulta, optimizando la carga de la sección de comentarios.
   *
   * @param elementoId ID del elemento consultado.
   * @return Lista de reseñas.
   */
  @Query(
      "SELECT r FROM Resena r JOIN FETCH r.usuario WHERE r.elemento.id = :elementoId ORDER BY r.fechaCreacion DESC")
  List<Resena> findByElementoIdOrderByFechaCreacionDesc(@Param("elementoId") Long elementoId);

  /**
   * Verifica si un usuario ya ha escrito una reseña para un elemento dado. Útil para evitar
   * duplicados o permitir la edición.
   */
  Optional<Resena> findByUsuarioIdAndElementoId(Long usuarioId, Long elementoId);
}
