package com.libratrack.api.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/** Servicio para la gestión de subida de archivos (imágenes) a Google Cloud Storage. */
@Service
public class FileStorageService {

  @Autowired private Storage storage;

  @Value("${gcs.bucket.name}")
  private String bucketName;

  /**
   * Sube un archivo al bucket configurado y devuelve su URL pública.
   *
   * @param file Archivo recibido del cliente.
   * @return URL pública del recurso subido.
   * @throws RuntimeException Si el archivo es inválido o falla la subida.
   */
  public String storeFile(MultipartFile file) {

    if (file.isEmpty()) {
      throw new RuntimeException("{exception.file.empty}");
    }

    String originalFileName =
        StringUtils.cleanPath(
            file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown");

    // Validación de seguridad básica
    if (originalFileName.contains("..")) {
      throw new RuntimeException("{exception.file.invalid_path}");
    }

    // Generar nombre único
    String extension = "";
    int dotIndex = originalFileName.lastIndexOf('.');
    if (dotIndex > 0) {
      extension = originalFileName.substring(dotIndex);
    }
    String uniqueFileName = UUID.randomUUID().toString() + extension;

    try {
      BlobId blobId = BlobId.of(bucketName, uniqueFileName);
      BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

      storage.create(blobInfo, file.getBytes());

      // Retornar URL pública directa
      return "https://storage.googleapis.com/" + bucketName + "/" + uniqueFileName;

    } catch (IOException e) {
      throw new RuntimeException("{exception.file.upload_error}", e);
    }
  }
}
