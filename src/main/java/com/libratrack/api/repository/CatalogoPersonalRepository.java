package com.libratrack.api.repository;

import com.libratrack.api.entity.CatalogoPersonal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogoPersonalRepository extends JpaRepository<CatalogoPersonal, Long> {

  List<CatalogoPersonal> findByUsuarioId(Long usuarioId);

  Optional<CatalogoPersonal> findByUsuarioIdAndElementoId(Long usuarioId, Long elementoId);
}
