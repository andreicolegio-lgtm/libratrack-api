package com.libratrack.api.service;

import com.libratrack.api.dto.ElementoResponseDTO;
import com.libratrack.api.dto.PaginatedResponse;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.UsuarioRepository;
import com.libratrack.api.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

  @Autowired private ElementoRepository elementoRepository;
  @Autowired private UsuarioRepository usuarioRepository;

  /**
   * Fetches elements created by the authenticated admin.
   *
   * @param page The page number.
   * @param size The page size.
   * @param search Optional search term to filter by title.
   * @return A paginated response of elements created by the admin.
   */
  public PaginatedResponse<ElementoResponseDTO> getMisElementosCreados(int page, int size, String search) {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long adminId = ((CustomUserDetails) principal).getId();

    Usuario admin =
        usuarioRepository
            .findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + adminId));

    PageRequest pageable = PageRequest.of(page, size);
    Page<Elemento> elementos;

    if (search != null && !search.isEmpty()) {
      elementos =
          elementoRepository.findByCreadorAndTituloContainingIgnoreCase(admin, search, pageable);
    } else {
      elementos = elementoRepository.findByCreador(admin, pageable);
    }

    // Map Elemento to ElementoResponseDTO
    Page<ElementoResponseDTO> dtoPage = elementos.map(ElementoResponseDTO::new);

    return new PaginatedResponse<>(
        dtoPage.getContent(),
        dtoPage.getTotalPages(),
        dtoPage.getTotalElements(),
        dtoPage.isLast(),
        dtoPage.getNumber(),
        dtoPage.getSize());
  }
}
