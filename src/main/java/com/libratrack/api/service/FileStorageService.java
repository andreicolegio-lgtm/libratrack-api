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

@Service
public class FileStorageService {

  @Autowired private Storage storage;

  @Value("${gcs.bucket.name}")
  private String bucketName;

  public String storeFile(MultipartFile file) {

    if (file.isEmpty()) {
      throw new RuntimeException("FILE_EMPTY");
    }

    String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
    String extension = "";

    try {
      if (originalFileName.contains("..")) {
        throw new RuntimeException("INVALID_FILE_NAME");
      }

      int dotIndex = originalFileName.lastIndexOf('.');
      if (dotIndex > 0) {
        extension = originalFileName.substring(dotIndex);
      }
      String uniqueFileName = UUID.randomUUID().toString() + extension;

      BlobId blobId = BlobId.of(bucketName, uniqueFileName);

      BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

      storage.create(blobInfo, file.getBytes());

      return "https://storage.googleapis.com/" + bucketName + "/" + uniqueFileName;

    } catch (IOException e) {
      throw new RuntimeException("FILE_SAVE_ERROR");
    }
  }
}
