package com.libratrack.api.repository;

import com.libratrack.api.entity.Tipo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long> {

  Optional<Tipo> findByNombre(String nombre);

  @Query("SELECT DISTINCT t FROM Tipo t LEFT JOIN FETCH t.generosPermitidos")
  List<Tipo> findAllWithGeneros();
}
