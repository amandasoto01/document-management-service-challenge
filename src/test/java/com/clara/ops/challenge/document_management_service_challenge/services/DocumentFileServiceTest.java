package com.clara.ops.challenge.document_management_service_challenge.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentRequest;
import com.clara.ops.challenge.document_management_service_challenge.entities.Document;
import com.clara.ops.challenge.document_management_service_challenge.exceptions.InvalidDocumentIdException;
import com.clara.ops.challenge.document_management_service_challenge.repositories.DocumentRepository;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class DocumentFileServiceTest {

  @Mock private MinioService minioService;

  @Mock private DocumentService documentService;

  @Mock private DocumentRepository documentRepository;

  @InjectMocks private DocumentFileService documentFileService;

  @Test
  void testUploadDocument_success() throws Exception {
    DocumentRequest documentRequest = new DocumentRequest("doc1", "user1", new String[] {"tag1"});
    MultipartFile file = mock(MultipartFile.class);

    String expectedFileName = "File uploaded successfully with id 1";
    when(minioService.uploadDocument(documentRequest, file))
        .thenReturn(CompletableFuture.completedFuture(expectedFileName));
    when(documentService.saveMetadataDocument(any(), any(), any())).thenReturn(1L);

    CompletableFuture<String> result =
        CompletableFuture.completedFuture(
            documentFileService.uploadAndPersist(documentRequest, file));

    assertNotNull(result);
    assertEquals(expectedFileName, result.get());
    verify(minioService, times(1)).uploadDocument(documentRequest, file);
  }

  @Test
  void testUploadDocument_expectedException_failure() {
    DocumentRequest documentRequest = new DocumentRequest("doc1", "user1", new String[] {"tag1"});
    MultipartFile file = mock(MultipartFile.class);

    when(minioService.uploadDocument(documentRequest, file))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Upload failed")));

    assertThrows(
        RuntimeException.class, () -> documentFileService.uploadAndPersist(documentRequest, file));

    verify(minioService, times(1)).uploadDocument(documentRequest, file);
  }

  @Test
  void testGetDownloadUrl_success() {
    Long documentId = 1L;
    String expectedUrl = "http://example.com/download/user1/doc1";
    Document document =
        Document.builder()
            .id(1L)
            .documentName("doc1")
            .userName("user1")
            .filePath("http://example.com/download/user1/doc1")
            .tags(new String[] {"tag1"})
            .build();
    when(documentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(document));
    assertNotNull(document);
    when(minioService.getDownloadUrl(document.getFilePath())).thenReturn(expectedUrl);

    String result = documentFileService.getDownloadUrl(documentId);

    assertNotNull(result);
    assertEquals(expectedUrl, result);
    verify(minioService, times(1)).getDownloadUrl(document.getFilePath());
  }

  @Test
  void testGetDownloadUrlInvalidId_failure() {
    Long invalidDocumentId = 999L; // Non-existent document ID
    when(documentRepository.findById(invalidDocumentId)).thenReturn(Optional.empty());

    assertThrows(
        InvalidDocumentIdException.class,
        () -> documentFileService.getDownloadUrl(invalidDocumentId));

    verify(documentRepository, times(1)).findById(invalidDocumentId);
    verifyNoInteractions(minioService);
  }
}
