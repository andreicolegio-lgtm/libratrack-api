package com.libratrack.api.service;

import com.libratrack.api.dto.CatalogoPersonalResponseDTO;
import com.libratrack.api.dto.CatalogoUpdateDTO;
import com.libratrack.api.entity.CatalogoPersonal;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ConflictException;
import com.libratrack.api.exception.ResourceNotFoundException;
import com.libratrack.api.model.EstadoPersonal;
import com.libratrack.api.repository.CatalogoPersonalRepository;
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.UsuarioRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogoPersonalService {

  @Autowired private CatalogoPersonalRepository catalogoRepo;
  @Autowired private UsuarioRepository usuarioRepo;
  @Autowired private ElementoRepository elementoRepo;

  @Transactional(readOnly = true)
  public List<CatalogoPersonalResponseDTO> getCatalogoByUsername(String username) {
    List<CatalogoPersonal> catalogo = catalogoRepo.findByUsuario_Username(username);

    return catalogo.stream().map(CatalogoPersonalResponseDTO::new).collect(Collectors.toList());
  }

  @Transactional
  public CatalogoPersonalResponseDTO addElementoAlCatalogo(String username, Long elementoId) {

    Usuario usuario =
        usuarioRepo
            .findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
    Elemento elemento =
        elementoRepo
            .findById(elementoId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Elemento no encontrado con id: " + elementoId));

    if (catalogoRepo.findByUsuarioIdAndElementoId(usuario.getId(), elemento.getId()).isPresent()) {
      throw new ConflictException("Este elemento ya está en tu catálogo.");
    }

    CatalogoPersonal nuevaEntrada = new CatalogoPersonal();
    nuevaEntrada.setUsuario(usuario);
    nuevaEntrada.setElemento(elemento);

    nuevaEntrada.setEstadoPersonal(EstadoPersonal.PENDIENTE);

    CatalogoPersonal entradaGuardada = catalogoRepo.save(nuevaEntrada);

    return new CatalogoPersonalResponseDTO(entradaGuardada);
  }

  @Transactional
  public CatalogoPersonalResponseDTO updateEntradaCatalogo(
      String username, Long elementoId, CatalogoUpdateDTO dto) {

    CatalogoPersonal entrada =
        catalogoRepo
            .findByUsuario_UsernameAndElemento_Id(username, elementoId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Este elemento no está en tu catálogo (Elemento ID: " + elementoId + ")."));

    if (dto.getEstadoPersonal() != null) {
      entrada.setEstadoPersonal(dto.getEstadoPersonal());
    }
    if (dto.getTemporadaActual() != null) {
      entrada.setTemporadaActual(dto.getTemporadaActual());
    }
    if (dto.getUnidadActual() != null) {
      entrada.setUnidadActual(dto.getUnidadActual());
    }
    if (dto.getCapituloActual() != null) {
      entrada.setCapituloActual(dto.getCapituloActual());
    }
    if (dto.getPaginaActual() != null) {
      entrada.setPaginaActual(dto.getPaginaActual());
    }

    CatalogoPersonal entradaGuardada = catalogoRepo.save(entrada);

    return new CatalogoPersonalResponseDTO(entradaGuardada);
  }

  @Transactional
  public void removeElementoDelCatalogo(String username, Long elementoId) {

    CatalogoPersonal entrada =
        catalogoRepo
            .findByUsuario_UsernameAndElemento_Id(username, elementoId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Este elemento no está en tu catálogo (Elemento ID: " + elementoId + ")."));

    catalogoRepo.delete(entrada);
  }
}
