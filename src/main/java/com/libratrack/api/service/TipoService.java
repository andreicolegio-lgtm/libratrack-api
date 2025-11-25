package com.libratrack.api.service;

import com.libratrack.api.entity.Tipo;
import com.libratrack.api.repository.TipoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TipoService {

  @Autowired private TipoRepository tipoRepository;

  @Transactional(readOnly = true)
  public List<Tipo> getAllTipos() {
    return tipoRepository.findAllWithGeneros();
  }

  public Tipo createTipo(Tipo tipo) throws Exception {
    return tipoRepository.save(tipo);
  }
}
