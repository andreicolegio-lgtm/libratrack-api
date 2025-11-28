package com.libratrack.api.repository;

import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Usuario;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio principal para la gestión de {@link Elemento}. Incluye funcionalidades avanzadas de
 * búsqueda y filtrado.
 */
@Repository
public interface ElementoRepository extends JpaRepository<Elemento, Long> {

  /**
   * Busca elementos cuyo título contenga la cadena proporcionada (case-insensitive). Útil para
   * autocompletado simple.
   */
  List<Elemento> findByTituloContainingIgnoreCase(String titulo);

  /**
   * Búsqueda avanzada con filtros dinámicos y paginación.
   *
   * <p>Permite filtrar por texto (título), tipos (Anime, Libro, etc.) y géneros. Utiliza {@code
   * DISTINCT} para evitar duplicados cuando un elemento tiene múltiples géneros coincidentes.
   *
   * @param searchText Texto parcial a buscar en el título.
   * @param types Lista de nombres de tipos permitidos (puede ser null).
   * @param genres Lista de nombres de géneros permitidos (puede ser null).
   * @param pageable Configuración de paginación.
   * @return Página de elementos filtrados.
   */
  @Query(
      value =
          "SELECT e FROM Elemento e "
              + "WHERE "
              + "(:searchText IS NULL OR LOWER(e.titulo) LIKE LOWER(CONCAT('%', :searchText, '%'))) "
              + "AND (:types IS NULL OR e.tipo.nombre IN :types) "
              + "AND (:genres IS NULL OR EXISTS ("
              + "  SELECT g FROM e.generos g WHERE g.nombre IN :genres))",
      countQuery =
          "SELECT COUNT(e) FROM Elemento e "
              + "WHERE "
              + "(:searchText IS NULL OR LOWER(e.titulo) LIKE LOWER(CONCAT('%', :searchText, '%'))) "
              + "AND (:types IS NULL OR e.tipo.nombre IN :types) "
              + "AND (:genres IS NULL OR EXISTS ("
              + "  SELECT g FROM e.generos g WHERE g.nombre IN :genres))")
  Page<Elemento> findElementosByFiltros(
      @Param("searchText") String searchText,
      @Param("types") List<String> types,
      @Param("genres") List<String> genres,
      Pageable pageable);

  /**
   * Fetches elements created by a specific user with pagination.
   *
   * @param creador The user who created the elements.
   * @param pageable Pagination configuration.
   * @return A page of elements created by the specified user.
   */
  Page<Elemento> findByCreador(Usuario creador, Pageable pageable);

  /**
   * Fetches elements created by a specific user and filters by title (case-insensitive).
   *
   * @param creador The user who created the elements.
   * @param titulo The title to filter by.
   * @param pageable Pagination configuration.
   * @return A page of elements created by the specified user and matching the title.
   */
  Page<Elemento> findByCreadorAndTituloContainingIgnoreCase(
      Usuario creador, String titulo, Pageable pageable);
}
