package com.libratrack.api.repository;

import com.libratrack.api.entity.PropuestaElemento;
import com.libratrack.api.model.EstadoPropuesta;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Repositorio para gestionar las sugerencias de contenido enviadas por los usuarios. */
@Repository
public interface PropuestaElementoRepository extends JpaRepository<PropuestaElemento, Long> {

  /**
   * Recupera todas las propuestas que coincidan con un estado específico (PENDIENTE, APROBADO,
   * RECHAZADO).
   *
   * <p>Carga automáticamente los datos del usuario proponente para mostrarlos en la interfaz de
   * moderación.
   *
   * @param estado Estado de la propuesta a filtrar.
   * @return Lista de propuestas coincidentes.
   */
  @Query(
      "SELECT p FROM PropuestaElemento p JOIN FETCH p.proponente WHERE p.estadoPropuesta = :estado")
  List<PropuestaElemento> findByEstadoPropuesta(@Param("estado") EstadoPropuesta estado);

  /**
   * Recupera propuestas filtradas por estado, texto, tipos y géneros.
   *
   * @param estado Estado de la propuesta a filtrar.
   * @param search Texto de búsqueda opcional en el título sugerido.
   * @param types Lista opcional de tipos sugeridos.
   * @param genres Géneros sugeridos opcionales (cadena que contiene).
   * @return Lista de propuestas coincidentes.
   */
  @Query(
      "SELECT p FROM PropuestaElemento p " +
      "JOIN FETCH p.proponente " +
      "LEFT JOIN FETCH p.revisor " +
      "WHERE p.estadoPropuesta = :estado " +
      "AND (:search IS NULL OR LOWER(p.tituloSugerido) LIKE LOWER(CONCAT('%', :search, '%'))) " +
      "AND (:types IS NULL OR p.tipoSugerido IN :types) " +
      "AND (:genres IS NULL OR p.generosSugeridos LIKE CONCAT('%', :genres, '%'))")
  Page<PropuestaElemento> searchPropuestas(
      @Param("estado") EstadoPropuesta estado,
      @Param("search") String search,
      @Param("types") List<String> types,
      @Param("genres") String genres,
      Pageable pageable);
}
