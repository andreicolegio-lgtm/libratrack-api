package com.libratrack.api.repository;

import com.libratrack.api.entity.PropuestaElemento;
import com.libratrack.api.model.EstadoPropuesta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropuestaElementoRepository extends JpaRepository<PropuestaElemento, Long> {

  List<PropuestaElemento> findByEstadoPropuesta(EstadoPropuesta estado);
}
