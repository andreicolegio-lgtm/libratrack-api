package com.libratrack.api.service;

import com.libratrack.api.entity.Tipo;
import com.libratrack.api.repository.TipoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TipoService {

  @Autowired private TipoRepository tipoRepository;

  public List<Tipo> getAllTipos() {
    return tipoRepository.findAll();
  }

  public Tipo createTipo(Tipo tipo) throws Exception {
    return tipoRepository.save(tipo);
  }
}
