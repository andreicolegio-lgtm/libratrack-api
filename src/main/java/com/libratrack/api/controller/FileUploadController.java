package com.libratrack.api.controller;

import com.libratrack.api.service.FileStorageService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/** Controlador para la gestión de subida de archivos multimedia (imágenes de perfil, portadas). */
@RestController
@RequestMapping("/api/uploads")
public class FileUploadController {

  @Autowired private FileStorageService fileStorageService;

  /**
   * Sube un archivo y devuelve su URL pública. Requiere autenticación para evitar spam de archivos.
   */
  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
    try {
      String fileUrl = fileStorageService.storeFile(file);
      return ResponseEntity.ok(Map.of("url", fileUrl));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "{exception.file.upload_error}", "details", e.getMessage()));
    }
  }
}
