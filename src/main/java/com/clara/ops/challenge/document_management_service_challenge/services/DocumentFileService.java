package com.clara.ops.challenge.document_management_service_challenge.services;

import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentUploadRequest;
import com.clara.ops.challenge.document_management_service_challenge.entities.Document;
import com.clara.ops.challenge.document_management_service_challenge.exceptions.InvalidDocumentIdException;
import com.clara.ops.challenge.document_management_service_challenge.repositories.DocumentRepository;
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
public class DocumentFileService {
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentFileService.class);

  @Autowired private final MinioService minioService;
  @Autowired private final DocumentService documentService;
  @Autowired private final DocumentRepository documentRepository;

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

  public String getDownloadUrl(Long documentId) {
    Document document =
        documentRepository
            .findById(documentId)
            .orElseThrow(
                () ->
                    new InvalidDocumentIdException(
                        "Document ID not found please enter a valid id"));
    return minioService.getDownloadUrl(document.getFilePath());
  }
}
