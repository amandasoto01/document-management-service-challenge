package com.clara.ops.challenge.document_management_service_challenge.services;

import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentUploadRequest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DocumentUploadService {
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentUploadService.class);

  @Autowired private final MinioService minioService;
  @Autowired private final DocumentService documentService;

  public String uploadAndPersist(DocumentUploadRequest documentRequest, MultipartFile file)
      throws ExecutionException, InterruptedException {

    CompletableFuture<String> cf =
        minioService
            .uploadDocument(documentRequest, file)
            .thenApply(
                fileUrl -> {
                  if (fileUrl == null || fileUrl.isEmpty()) {
                    return "File not created";
                  }

                  long id = documentService.uploadDocument(documentRequest, file, fileUrl);
                  if (id == -1) {
                    return "Error saving document in database";
                  }

                  return "File uploaded successfully with id " + id;
                })
            .exceptionally(
                ex -> {
                  LOGGER.error("Error uploading document", ex);
                  return "Error uploading file";
                });

    return cf.get();
  }
}
