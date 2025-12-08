package com.libratrack.api.service;

import com.libratrack.api.dto.ElementoResponseDTO;
import com.libratrack.api.dto.PaginatedResponse;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.UsuarioRepository;
import com.libratrack.api.security.CustomUserDetails;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
   * @param types Optional list of types to filter by.
   * @param genres Optional list of genres to filter by.
   * @param sortMode The field to sort by.
   * @param isAscending True for ascending order, false for descending.
   * @return A paginated response of elements created by the admin.
   */
  public PaginatedResponse<ElementoResponseDTO> getMisElementosCreados(
      int page,
      int size,
      String search,
      List<String> types,
      List<String> genres,
      String sortMode,
      boolean isAscending) {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long adminId = ((CustomUserDetails) principal).getId();

    Usuario admin =
        usuarioRepository
            .findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + adminId));

    PageRequest pageable =
        PageRequest.of(
            page, size, Sort.by(isAscending ? Sort.Direction.ASC : Sort.Direction.DESC, sortMode));

    Specification<Elemento> spec =
        (root, query, criteriaBuilder) -> {
          var predicates = new ArrayList<>();
          predicates.add(criteriaBuilder.equal(root.get("creador"), admin));

          if (search != null && !search.isEmpty()) {
            predicates.add(
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("titulo")), "%" + search.toLowerCase() + "%"));
          }

          if (types != null && !types.isEmpty()) {
            predicates.add(root.get("tipo").in(types));
          }

          if (genres != null && !genres.isEmpty()) {
            predicates.add(root.join("generos").in(genres));
          }

          return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

    Page<Elemento> elementos = elementoRepository.findAll(spec, pageable);

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

  /**
   * Fetches elements created by a specific admin with advanced filtering and sorting options.
   *
   * @param adminId The ID of the admin whose elements are to be fetched.
   * @param page The page number.
   * @param size The page size.
   * @param search Optional search term to filter by title.
   * @param types Optional list of types to filter by.
   * @param genres Optional list of genres to filter by.
   * @param sortMode The field to sort by.
   * @param isAscending True for ascending order, false for descending.
   * @return A paginated response of elements created by the specified admin.
   */
  @Transactional(readOnly = true)
  public PaginatedResponse<ElementoResponseDTO> getElementosCreados(
      Long adminId,
      int page,
      int size,
      String search,
      List<String> types,
      List<String> genres,
      String sortMode,
      boolean isAscending) {

    // Lógica de Ordenamiento
    Sort sort = Sort.by("id").descending(); // Default (Reciente)
    if ("ALPHA".equals(sortMode)) {
      sort = isAscending ? Sort.by("titulo").ascending() : Sort.by("titulo").descending();
    } else { // DATE
      sort = isAscending ? Sort.by("id").ascending() : Sort.by("id").descending();
    }
    PageRequest pageable = PageRequest.of(page, size, sort);

    // Determine if the user is an admin
    boolean isAdmin =
        SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    // Fetch history elements based on user role and filters
    Page<Elemento> elementos =
        elementoRepository.findHistoryByFilters(
            adminId,
            isAdmin,
            search,
            (types == null || types.isEmpty()) ? null : types,
            (genres == null || genres.isEmpty()) ? null : genres,
            pageable);

    // Inicialización Forzada de Relaciones (Evita 'Desconocido' en Frontend)
    elementos.forEach(
        e -> {
          Hibernate.initialize(e.getTipo());
          Hibernate.initialize(e.getGeneros());
          Hibernate.initialize(e.getCreador());
        });

    // Mapeo manual a DTO paginado
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
