package com.clara.ops.challenge.document_management_service_challenge.services;

import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentRequest;
import com.clara.ops.challenge.document_management_service_challenge.entities.Document;
import com.clara.ops.challenge.document_management_service_challenge.exceptions.InvalidDocumentIdException;
import com.clara.ops.challenge.document_management_service_challenge.repositories.DocumentRepository;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DocumentFileService {
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentFileService.class);
  private final MinioService minioService;
  private final DocumentService documentService;
  private final DocumentRepository documentRepository;

  public String uploadAndPersist(DocumentRequest documentRequest, MultipartFile file) {
    LOGGER.info("DocumentFileService: uploadAndPersist method...");

    if (documentRequest.getDocumentName() == null || documentRequest.getDocumentName().isEmpty()) {
      documentRequest.setDocumentName(file.getOriginalFilename());
    }

    CompletableFuture<String> cf =
        minioService
            .uploadDocument(documentRequest, file)
            .thenApply(
                fileUrl -> {
                  if (fileUrl == null || fileUrl.isEmpty()) {
                    return "File not created";
                  }

                  long id = documentService.saveMetadataDocument(documentRequest, file, fileUrl);
                  return "File uploaded successfully with id " + id;
                })
            .exceptionally(
                ex -> {
                  LOGGER.error("Error uploading document", ex);
                  throw new RuntimeException(ex);
                });

    try {
      return cf.get();
    } catch (ExecutionException e) {
      LOGGER.error("Execution exception during upload {}", e.getMessage());
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.error("Upload interrupted {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  public String getDownloadUrl(Long documentId) {
    LOGGER.info("DocumentFileService: get download url method...");
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
