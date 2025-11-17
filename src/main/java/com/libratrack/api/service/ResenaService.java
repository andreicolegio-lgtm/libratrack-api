package com.libratrack.api.service;

import com.libratrack.api.dto.ResenaDTO;
import com.libratrack.api.dto.ResenaResponseDTO;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Resena;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ConflictException;
import com.libratrack.api.exception.ResourceNotFoundException;
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.ResenaRepository;
import com.libratrack.api.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResenaService {

  @Autowired private ResenaRepository resenaRepo;
  @Autowired private UsuarioRepository usuarioRepo;
  @Autowired private ElementoRepository elementoRepo;

  @Transactional(readOnly = true)
  public List<ResenaResponseDTO> getResenasByElementoId(Long elementoId) {
    if (!elementoRepo.existsById(elementoId)) {
      throw new ResourceNotFoundException("ELEMENT_NOT_FOUND");
    }

    List<Resena> resenas = resenaRepo.findByElementoIdOrderByFechaCreacionDesc(elementoId);

    return resenas.stream().map(ResenaResponseDTO::new).collect(Collectors.toList());
  }

  @Transactional
  public ResenaResponseDTO createResena(ResenaDTO dto, String username) {

    Usuario usuario =
      usuarioRepo
        .findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("INVALID_USER_TOKEN"));

    Elemento elemento =
      elementoRepo
        .findById(dto.getElementoId())
        .orElseThrow(
          () ->
            new ResourceNotFoundException("ELEMENT_NOT_FOUND"));

    Optional<Resena> existingResena =
        resenaRepo.findByUsuarioIdAndElementoId(usuario.getId(), elemento.getId());
    if (existingResena.isPresent()) {
      throw new ConflictException("ALREADY_REVIEWED");
    }

    Resena nuevaResena = new Resena();
    nuevaResena.setUsuario(usuario);
    nuevaResena.setElemento(elemento);

    if (dto.getValoracion() == null || dto.getValoracion() < 1 || dto.getValoracion() > 5) {
      throw new ConflictException("INVALID_RATING_RANGE");
    }
    nuevaResena.setValoracion(dto.getValoracion());
    nuevaResena.setTextoResena(dto.getTextoResena());

    Resena resenaGuardada = resenaRepo.save(nuevaResena);

    return new ResenaResponseDTO(resenaGuardada);
  }
}
