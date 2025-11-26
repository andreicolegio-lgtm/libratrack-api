package com.libratrack.api.repository;

import com.libratrack.api.entity.PropuestaElemento;
import com.libratrack.api.model.EstadoPropuesta;
import java.util.List;
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
}
