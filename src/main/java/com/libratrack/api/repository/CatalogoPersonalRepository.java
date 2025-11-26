package com.libratrack.api.repository;

import com.libratrack.api.entity.CatalogoPersonal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para gestionar las entradas del catálogo personal de los usuarios. Proporciona
 * métodos optimizados para recuperar relaciones.
 */
@Repository
public interface CatalogoPersonalRepository extends JpaRepository<CatalogoPersonal, Long> {

  /**
   * Recupera todas las entradas del catálogo de un usuario específico.
   *
   * <p><strong>Nota:</strong> Utiliza una query personalizada con {@code JOIN FETCH} para cargar
   * los datos del {@link com.libratrack.api.entity.Elemento} asociado en una sola consulta,
   * mejorando drásticamente el rendimiento al evitar el problema N+1.
   *
   * @param usuarioId ID del usuario propietario del catálogo.
   * @return Lista de entradas de catálogo con sus elementos cargados.
   */
  @Query(
      "SELECT cp FROM CatalogoPersonal cp "
          + "JOIN FETCH cp.elemento e "
          + "LEFT JOIN FETCH e.tipo "
          + "WHERE cp.usuario.id = :usuarioId")
  List<CatalogoPersonal> findByUsuarioId(@Param("usuarioId") Long usuarioId);

  /**
   * Busca una entrada específica por usuario y elemento (sin fetch adicional). Útil para
   * comprobaciones de existencia rápidas.
   */
  Optional<CatalogoPersonal> findByUsuarioIdAndElementoId(Long usuarioId, Long elementoId);

  /**
   * Busca una entrada específica cargando todas sus relaciones necesarias para edición o detalle.
   */
  @Query(
      "SELECT cp FROM CatalogoPersonal cp "
          + "JOIN FETCH cp.usuario u "
          + "JOIN FETCH cp.elemento e "
          + "LEFT JOIN FETCH e.tipo "
          + "WHERE u.id = :usuarioId AND e.id = :elementoId")
  Optional<CatalogoPersonal> findByUsuarioIdAndElementoIdWithFetch(
      @Param("usuarioId") Long usuarioId, @Param("elementoId") Long elementoId);
}
