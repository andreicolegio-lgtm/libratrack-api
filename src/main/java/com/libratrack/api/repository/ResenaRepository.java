package com.libratrack.api.repository;

import com.libratrack.api.entity.Resena;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

  List<Resena> findByElementoIdOrderByFechaCreacionDesc(Long elementoId);

  Optional<Resena> findByUsuarioIdAndElementoId(Long usuarioId, Long elementoId);
}
