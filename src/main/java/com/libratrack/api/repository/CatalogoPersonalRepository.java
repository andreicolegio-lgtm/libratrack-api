package com.libratrack.api.repository;

import com.libratrack.api.entity.CatalogoPersonal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogoPersonalRepository extends JpaRepository<CatalogoPersonal, Long> {

  List<CatalogoPersonal> findByUsuarioId(Long usuarioId);

  Optional<CatalogoPersonal> findByUsuarioIdAndElementoId(Long usuarioId, Long elementoId);

  @Query("SELECT cp FROM CatalogoPersonal cp "
      + "JOIN FETCH cp.usuario u "
      + "JOIN FETCH cp.elemento e "
      + "WHERE u.id = :usuarioId AND e.id = :elementoId")
  Optional<CatalogoPersonal> findByUsuarioIdAndElementoIdWithFetch(
      @Param("usuarioId") Long usuarioId, @Param("elementoId") Long elementoId);
}
