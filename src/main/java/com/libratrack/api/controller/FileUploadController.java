package com.libratrack.api.controller;

import com.libratrack.api.service.FileStorageService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/uploads")
public class FileUploadController {

  @Autowired private FileStorageService fileStorageService;

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {

    try {
      String fileUrl = fileStorageService.storeFile(file);
      return ResponseEntity.ok(Map.of("url", fileUrl));

    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "FILE_UPLOAD_ERROR", "details", e.getMessage()));
    }
  }
}
