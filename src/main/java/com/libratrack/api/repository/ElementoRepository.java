package com.libratrack.api.repository;

import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Usuario;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

  /**
   * Fetches elements based on dynamic filters and pagination.
   *
   * @param spec Specification for dynamic filtering.
   * @param pageable Pagination configuration.
   * @return A page of elements matching the specification.
   */
  Page<Elemento> findAll(Specification<Elemento> spec, Pageable pageable);

  /**
   * Fetches elements created by a specific user with advanced filtering.
   *
   * @param creadorId The ID of the user who created the elements.
   * @param search Optional search term to filter by title.
   * @param types Optional list of types to filter by.
   * @param genres Optional list of genres to filter by.
   * @param pageable Pagination configuration.
   * @return A page of elements matching the criteria.
   */
  @Query(
      "SELECT e FROM Elemento e WHERE e.creador.id = :creadorId "
          + "AND (:search IS NULL OR LOWER(e.titulo) LIKE LOWER(CONCAT('%', :search, '%'))) "
          + "AND (:types IS NULL OR e.tipo.nombre IN :types) "
          + "AND (:genres IS NULL OR EXISTS (SELECT g FROM e.generos g WHERE g.nombre IN :genres))")
  Page<Elemento> findByCreadorAndFiltros(
      @Param("creadorId") Long creadorId,
      @Param("search") String search,
      @Param("types") List<String> types,
      @Param("genres") List<String> genres,
      Pageable pageable);

  /**
   * Fetches history elements based on user role and filters.
   *
   * @param userId The ID of the user making the request.
   * @param isAdmin Whether the user is an admin.
   * @param search Optional search term to filter by title.
   * @param types Optional list of types to filter by.
   * @param genres Optional list of genres to filter by.
   * @param pageable Pagination configuration.
   * @return A page of elements matching the criteria.
   */
  @Query(
      "SELECT e FROM Elemento e JOIN e.creador u WHERE "
          + "((:isAdmin = true AND (u.esModerador = true OR u.esAdministrador = true)) OR u.id = :userId) "
          + "AND (:search IS NULL OR LOWER(e.titulo) LIKE LOWER(CONCAT('%', :search, '%'))) "
          + "AND (:types IS NULL OR e.tipo.nombre IN :types) "
          + "AND (:genres IS NULL OR EXISTS (SELECT g FROM e.generos g WHERE g.nombre IN :genres)) "
          + "AND (e.creadoDesdePropuesta = false OR e.creadoDesdePropuesta IS NULL)")
  Page<Elemento> findHistoryByFilters(
      @Param("userId") Long userId,
      @Param("isAdmin") boolean isAdmin,
      @Param("search") String search,
      @Param("types") List<String> types,
      @Param("genres") List<String> genres,
      Pageable pageable);

  /**
   * Searches for public elements with sorting, filtering, and eager loading to avoid null data issues.
   *
   * @param search The search term for the element title.
   * @param types The types to filter by.
   * @param genres The genres to filter by.
   * @param pageable Pagination configuration.
   * @return A page of elements matching the search criteria.
   */
  @Query("SELECT e FROM Elemento e " +
         "LEFT JOIN FETCH e.tipo " +      // <--- CLAVE PARA EVITAR 'TIPO DESCONOCIDO'
         "LEFT JOIN FETCH e.generos " +   // <--- CLAVE PARA EVITAR 'GENEROS VACIOS'
         "WHERE (:search IS NULL OR LOWER(e.titulo) LIKE LOWER(CONCAT('%', :search, '%'))) " +
         "AND (:types IS NULL OR e.tipo.nombre IN :types) " +
         "AND (:genres IS NULL OR EXISTS (SELECT g FROM e.generos g WHERE g.nombre IN :genres))")
  Page<Elemento> searchPublicElementos(
      @Param("search") String search,
      @Param("types") List<String> types,
      @Param("genres") List<String> genres,
      Pageable pageable);
}
