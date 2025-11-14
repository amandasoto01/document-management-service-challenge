package com.clara.ops.challenge.document_management_service_challenge.controllers;

import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentRequest;
import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentResponse;
import com.clara.ops.challenge.document_management_service_challenge.services.DocumentFileService;
import com.clara.ops.challenge.document_management_service_challenge.services.DocumentService;
import com.clara.ops.challenge.document_management_service_challenge.utils.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.concurrent.ExecutionException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private final DocumentService documentService;
  private final DocumentFileService documentUploadService;

  @Operation(
      tags = "Uploads document",
      description = "Uploads document to minio and persists metadata to database",
      responses = {@ApiResponse(description = "Success", responseCode = "200")})
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> uploadDocument(
      @RequestPart("metadata") @Valid DocumentRequest documentRequest,
      @RequestPart("file") @NotNull(message = "file is required") MultipartFile file)
      throws ExecutionException, InterruptedException {
    LOGGER.info("Document controller ... upload document");

    FileUtils.validateFile(file);
    return ResponseEntity.ok(documentUploadService.uploadAndPersist(documentRequest, file));
  }

  @Operation(
      tags = "Get documents",
      description =
          "Get documents filtered by userName, documentName, order by createdAt descending order"
              + " with pagination",
      responses = {@ApiResponse(description = "Success", responseCode = "200")})
  @GetMapping(path = "filter")
  public ResponseEntity<Page<DocumentResponse>> getDocuments(
      @RequestParam(required = false) String userName,
      @RequestParam(required = false) String documentName,
      @RequestParam(required = false) String[] tags,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    LOGGER.info("Document controller .. get documents");
    return ResponseEntity.ok(
        documentService.getFilteredDocuments(userName, documentName, page, size));
  }

  @Operation(
      tags = "Get link",
      description = "Get download link to download document by documentId",
      responses = {
        @ApiResponse(description = "Success", responseCode = "200"),
        @ApiResponse(description = "Bad Request", responseCode = "400")
      })
  @GetMapping(path = "/download/{documentId}")
  public ResponseEntity<String> getDownloadUrl(@PathVariable Long documentId) {
    LOGGER.info("Document controller ... get download url");
    return ResponseEntity.ok(documentUploadService.getDownloadUrl(documentId));
  }
}
