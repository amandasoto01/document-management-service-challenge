package com.clara.ops.challenge.document_management_service_challenge.controllers;

import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentResponse;
import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentUploadRequest;
import com.clara.ops.challenge.document_management_service_challenge.services.DocumentService;
import com.clara.ops.challenge.document_management_service_challenge.services.DocumentUploadService;
import com.clara.ops.challenge.document_management_service_challenge.services.MinioService;
import com.clara.ops.challenge.document_management_service_challenge.utils.FileUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.concurrent.ExecutionException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/document")
@Validated
@AllArgsConstructor
public class DocumentController {
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

  @Autowired private FileUtils fileUtils;

  @Autowired private final MinioService minioService;

  @Autowired private final DocumentService documentService;

  @Autowired private final DocumentUploadService documentUploadService;

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> uploadDocument(
      @RequestPart("metadata") @Valid DocumentUploadRequest documentRequest,
      @RequestPart("file") @NotNull(message = "file is required") MultipartFile file)
      throws ExecutionException, InterruptedException {
    LOGGER.info("Upload endpoint called...");
    LOGGER.info("metadata: {}", documentRequest.getDocumentName());
    LOGGER.info("file: {}", file.getOriginalFilename());
    LOGGER.info("content type: {}", file.getContentType());

    FileUtils.validateFile(file);
    return ResponseEntity.ok(documentUploadService.uploadAndPersist(documentRequest, file));
  }

  @GetMapping(path = "filter")
  public ResponseEntity<Page<DocumentResponse>> getDocuments(
      @RequestParam(required = false) String userName,
      @RequestParam(required = false) String documentName,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    LOGGER.info("Get documents controller");
    return ResponseEntity.ok(
        documentService.getFilteredDocuments(userName, documentName, page, size));
  }
}
