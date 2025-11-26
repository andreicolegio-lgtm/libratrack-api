package com.libratrack.api.service;

import com.libratrack.api.entity.Tipo;
import com.libratrack.api.repository.TipoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Servicio para la gestión de Tipos de contenido (Anime, Libro, etc.). */
@Service
public class TipoService {

  @Autowired private TipoRepository tipoRepository;

  /** Obtiene todos los tipos y carga sus géneros permitidos en una sola transacción. */
  @Transactional(readOnly = true)
  public List<Tipo> getAllTipos() {
    return tipoRepository.findAllWithGeneros();
  }

  @Transactional
  public Tipo createTipo(Tipo tipo) {
    return tipoRepository.save(tipo);
  }
}
