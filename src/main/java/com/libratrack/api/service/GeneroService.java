package com.libratrack.api.service;

import com.libratrack.api.entity.Genero;
import com.libratrack.api.repository.GeneroRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeneroService {

  @Autowired private GeneroRepository generoRepository;

  public List<Genero> getAllGeneros() {
    return generoRepository.findAll();
  }

  public Genero createGenero(Genero genero) throws Exception {
    return generoRepository.save(genero);
  }
}
