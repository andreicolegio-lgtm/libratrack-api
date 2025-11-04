package com.libratrack.api.repository;

import com.libratrack.api.entity.PropuestaElemento;
import com.libratrack.api.model.EstadoPropuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropuestaElementoRepository extends JpaRepository<PropuestaElemento, Long> {

    /**
     * Busca todas las propuestas que tengan un estado específico.
     * Esto es fundamental para el Panel de Moderación (RF14).
     *
     * @param estado El estado a buscar (PENDIENTE, APROBADO, RECHAZADO).
     * @return Una lista de propuestas que coinciden con ese estado.
     */
    List<PropuestaElemento> findByEstadoPropuesta(EstadoPropuesta estado);

}