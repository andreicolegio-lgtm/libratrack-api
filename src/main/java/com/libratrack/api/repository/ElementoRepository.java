package com.libratrack.api.repository;

import com.libratrack.api.entity.Elemento;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ElementoRepository extends JpaRepository<Elemento, Long> {

  List<Elemento> findByTituloContainingIgnoreCase(String titulo);

  @Query(
      value =
          "SELECT DISTINCT e FROM Elemento e "
              + "LEFT JOIN FETCH e.tipo "
              + "LEFT JOIN FETCH e.generos "
              + "LEFT JOIN e.tipo t_filter "
              + "LEFT JOIN e.generos g_filter "
              + "WHERE "
              + "(LOWER(e.titulo) LIKE LOWER(CONCAT('%', COALESCE(:searchText, ''), '%'))) "
              + "AND (:types IS NULL OR t_filter.nombre IN :types) "
              + "AND (:genres IS NULL OR g_filter.nombre IN :genres)",
      countQuery =
          "SELECT COUNT(DISTINCT e.id) FROM Elemento e "
              + "LEFT JOIN e.tipo t_filter "
              + "LEFT JOIN e.generos g_filter "
              + "WHERE "
              + "(LOWER(e.titulo) LIKE LOWER(CONCAT('%', COALESCE(:searchText, ''), '%'))) "
              + "AND (:types IS NULL OR t_filter.nombre IN :types) "
              + "AND (:genres IS NULL OR g_filter.nombre IN :genres)")
  Page<Elemento> findElementosByFiltros(
      @Param("searchText") String searchText,
      @Param("types") List<String> types,
      @Param("genres") List<String> genres,
      Pageable pageable);
}
