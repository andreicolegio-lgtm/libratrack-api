package com.libratrack.api.repository;

import com.libratrack.api.entity.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    /**
     * Busca todas las reseñas de un elemento específico,
     * ordenadas por fecha de creación descendente (la más nueva primero).
     *
     * @param elementoId El ID del elemento.
     * @return Una lista de reseñas para ese elemento.
     */
    List<Resena> findByElementoIdOrderByFechaCreacionDesc(Long elementoId);

    /**
     * Busca una reseña específica de un usuario para un elemento.
     * (Necesario para cumplir la restricción de 1 reseña por usuario/elemento).
     *
     * @param usuarioId El ID del usuario.
     * @param elementoId El ID del elemento.
     * @return Un Optional que contendrá la reseña si ya existe.
     */
    Optional<Resena> findByUsuarioIdAndElementoId(Long usuarioId, Long elementoId);
}