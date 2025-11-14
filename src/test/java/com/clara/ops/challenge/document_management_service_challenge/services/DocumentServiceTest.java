package com.clara.ops.challenge.document_management_service_challenge.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentRequest;
import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentResponse;
import com.clara.ops.challenge.document_management_service_challenge.entities.Document;
import com.clara.ops.challenge.document_management_service_challenge.exceptions.DatabaseSaveException;
import com.clara.ops.challenge.document_management_service_challenge.repositories.DocumentRepository;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

  @Mock private DocumentRepository documentRepository;

  @InjectMocks private DocumentService documentService;

  @Test
  void testSaveMetadataDocument_newDocument() {
    DocumentRequest documentRequest = new DocumentRequest("user1", "doc1", new String[] {"tag1"});
    MultipartFile file = mock(MultipartFile.class);
    when(file.getSize()).thenReturn(1024L);
    when(file.getContentType()).thenReturn("application/pdf");

    Document document =
        Document.builder()
            .id(1L)
            .documentName("doc1")
            .userName("user1")
            .tags(new String[] {"tag1"})
            .filePath("fileUrl")
            .fileSize(1024L)
            .fileType("application/pdf")
            .build();

    when(documentRepository.findByUserNameAndDocumentName("user1", "doc1"))
        .thenReturn(Optional.empty());
    when(documentRepository.save(any(Document.class))).thenReturn(document);

    Long documentId = documentService.saveMetadataDocument(documentRequest, file, "fileUrl");

    assertNotNull(documentId);
    verify(documentRepository, times(1)).save(any(Document.class));
  }

  @Test
  void testSaveMetadataDocument_existingDocument() {
    // Arrange
    DocumentRequest documentRequest = new DocumentRequest("user1", "doc1", new String[] {"tag1"});
    MultipartFile file = mock(MultipartFile.class);
    when(file.getSize()).thenReturn(1024L);
    when(file.getContentType()).thenReturn("application/pdf");

    Document existingDocument =
        Document.builder()
            .id(1L)
            .documentName("doc1")
            .userName("user1")
            .tags(new String[] {"tag1"})
            .filePath("oldPath")
            .fileSize(512L)
            .fileType("text/plain")
            .build();

    when(documentRepository.findByUserNameAndDocumentName("user1", "doc1"))
        .thenReturn(Optional.of(existingDocument));
    when(documentRepository.save(any(Document.class))).thenReturn(existingDocument);

    Long documentId = documentService.saveMetadataDocument(documentRequest, file, "fileUrl");

    assertNotNull(documentId);
    verify(documentRepository, times(1)).save(any(Document.class));
  }

  @Test
  void testSaveMetadataDocument_throwsDatabaseSaveException() {
    DocumentRequest documentRequest = new DocumentRequest("doc1", "user1", new String[] {"tag1"});
    MultipartFile file = mock(MultipartFile.class);
    when(file.getSize()).thenReturn(1024L);
    when(file.getContentType()).thenReturn("application/pdf");

    when(documentRepository.save(any(Document.class))).thenThrow(new RuntimeException());

    assertThrows(
        DatabaseSaveException.class,
        () -> documentService.saveMetadataDocument(documentRequest, file, "fileUrl"));
  }

  @Test
  void testGetFilteredDocuments_allFields() {
    Document document =
        Document.builder()
            .id(1L)
            .documentName("doc1")
            .userName("user1")
            .tags(new String[] {"tag1"})
            .build();

    Page<Document> documentPage = new PageImpl<>(Collections.singletonList(document));
    when(documentRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(documentPage);

    Page<DocumentResponse> result = documentService.getFilteredDocuments("user1", "doc1", 0, 10);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(documentRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
  }
}
