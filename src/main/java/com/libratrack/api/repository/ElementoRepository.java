package com.libratrack.api.repository;

import com.libratrack.api.entity.Elemento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
// NUEVAS IMPORTACIONES
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ElementoRepository extends JpaRepository<Elemento, Long> {
    
    // --- MÉTODO ANTIGUO (Lo dejamos por si lo usa otro servicio) ---
    List<Elemento> findByTituloContainingIgnoreCase(String titulo);

    // --- NUEVO MÉTODO DE BÚSQUEDA PAGINADO ---
    /**
     * Busca elementos filtrando por todos los criterios y devuelve un resultado paginado.
     * Esta consulta es la implementación de rendimiento de RF09.
     *
     * @param searchText Texto a buscar en el título (ignora mayúsculas/minúsculas).
     * @param tipoName Nombre del Tipo a filtrar.
     * @param generoName Nombre del Género a filtrar.
     * @param pageable Objeto de Spring que contiene la información de paginación (page, size).
     * @return Una 'Page' (página) de Elementos que coinciden.
     */
    @Query("SELECT e FROM Elemento e " +
           "LEFT JOIN e.tipo t " +
           "LEFT JOIN e.generos g " +
           "WHERE " +
           // 1. Filtro de Título (searchText)
           // COALESCE evita un error si el parámetro es null
           "(LOWER(e.titulo) LIKE LOWER(CONCAT('%', COALESCE(:searchText, ''), '%'))) " +
           
           // 2. Filtro de Tipo (tipoName)
           "AND (:tipoName IS NULL OR t.nombre = :tipoName) " +
           
           // 3. Filtro de Género (generoName)
           "AND (:generoName IS NULL OR g.nombre = :generoName) " +
           
           // Agrupamos para evitar duplicados si un elemento tiene múltiples géneros
           "GROUP BY e.id") 
    Page<Elemento> findElementosByFiltros(
            @Param("searchText") String searchText, 
            @Param("tipoName") String tipoName, 
            @Param("generoName") String generoName, 
            Pageable pageable);
}