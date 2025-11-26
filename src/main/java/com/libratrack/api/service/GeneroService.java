package com.libratrack.api.service;

import com.libratrack.api.entity.Genero;
import com.libratrack.api.repository.GeneroRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio simple para la administración de Géneros. Utilizado principalmente por paneles de
 * administración.
 */
@Service
public class GeneroService {

  @Autowired private GeneroRepository generoRepository;

  @Transactional(readOnly = true)
  public List<Genero> getAllGeneros() {
    return generoRepository.findAll();
  }

  @Transactional
  public Genero createGenero(Genero genero) {
    // Aquí se podrían añadir validaciones extra si fuera necesario (ej. duplicados)
    return generoRepository.save(genero);
  }
}
