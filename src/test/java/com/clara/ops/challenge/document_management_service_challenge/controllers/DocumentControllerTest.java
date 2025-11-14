package com.clara.ops.challenge.document_management_service_challenge.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentRequest;
import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentResponse;
import com.clara.ops.challenge.document_management_service_challenge.exceptions.FileSizeLimitException;
import com.clara.ops.challenge.document_management_service_challenge.exceptions.InvalidDocumentIdException;
import com.clara.ops.challenge.document_management_service_challenge.exceptions.InvalidFormatException;
import com.clara.ops.challenge.document_management_service_challenge.services.DocumentFileService;
import com.clara.ops.challenge.document_management_service_challenge.services.DocumentService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {

  @Mock private DocumentService documentService;

  @Mock private DocumentFileService documentFileService;

  @InjectMocks private DocumentController documentController;

  private DocumentRequest documentRequest;

  @BeforeEach
  void setUp() {
    documentRequest =
        DocumentRequest.builder()
            .userName("user1")
            .documentName("doc1")
            .tags(List.of("tag1"))
            .build();
  }

  @Test
  void testUploadDocument_return200() throws ExecutionException, InterruptedException {
    MultipartFile file =
        new MockMultipartFile("file", "test.pdf", "application/pdf", "Test Content".getBytes());
    when(documentFileService.uploadAndPersist(documentRequest, file))
        .thenReturn("File uploaded successfully with id 1");

    ResponseEntity<String> response = documentController.uploadDocument(documentRequest, file);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("File uploaded successfully with id 1", response.getBody());
    verify(documentFileService, times(1)).uploadAndPersist(documentRequest, file);
  }

  @Test
  void testUploadDocument_invalidFormatException_error() {
    MultipartFile file = null;

    assertThrows(
        InvalidFormatException.class,
        () -> documentController.uploadDocument(documentRequest, file));
  }

  @Test
  void testUploadDocument_fileSizeLimitException_error() {
    DocumentRequest documentRequest = new DocumentRequest("user1", "doc1", List.of("tag1"));
    MultipartFile file =
        new MockMultipartFile("file", "test.pdf", "application/pdf", "Test Content".getBytes()) {
          @Override
          public long getSize() {
            return 600 * 1024 * 1024; // 600 MB
          }
        };

    assertThrows(
        FileSizeLimitException.class,
        () -> documentController.uploadDocument(documentRequest, file));
  }

  @Test
  void testGetDocuments_return200() {
    String userName = "user1";
    String documentName = "doc1";
    List<String> tags = new ArrayList<>();
    tags.add("tag1");
    int page = 0;
    int size = 10;

    Page<DocumentResponse> mockPage = mock(Page.class);
    when(documentService.getFilteredDocuments(userName, documentName, tags, page, size))
        .thenReturn(mockPage);

    ResponseEntity<Page<DocumentResponse>> response =
        documentController.getDocuments(userName, documentName, tags, page, size);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(mockPage, response.getBody());
    verify(documentService, times(1))
        .getFilteredDocuments(userName, documentName, tags, page, size);
  }

  @Test
  void testGetDocuments_returnError() {
    String userName = "user1";
    String documentName = "doc1";
    List<String> tags = List.of("tag1");
    int page = 0;
    int size = 10;

    when(documentService.getFilteredDocuments(userName, documentName, tags, page, size))
        .thenThrow(new RuntimeException("Error fetching documents"));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> documentController.getDocuments(userName, documentName, tags, page, size));
    assertEquals("Error fetching documents", exception.getMessage());
    verify(documentService, times(1))
        .getFilteredDocuments(userName, documentName, tags, page, size);
  }

  @Test
  void testDownloadDocument_return200() {
    Long id = 1L;

    when(documentFileService.getDownloadUrl(id)).thenReturn("https://minio/test.pdf");

    ResponseEntity<String> response = documentController.getDownloadUrl(id);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("https://minio/test.pdf", response.getBody());

    verify(documentFileService, times(1)).getDownloadUrl(id);
  }

  @Test
  void testDownloadDocument_documentNotFound_throwsInvalidDocumentIdException() {
    Long id = 999L;

    when(documentFileService.getDownloadUrl(id))
        .thenThrow(new InvalidDocumentIdException("Document not found"));

    InvalidDocumentIdException ex =
        assertThrows(InvalidDocumentIdException.class, () -> documentController.getDownloadUrl(id));

    assertEquals("Document not found", ex.getMessage());
    verify(documentFileService, times(1)).getDownloadUrl(id);
  }
}
