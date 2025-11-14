package com.clara.ops.challenge.document_management_service_challenge.services;

import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentRequest;
import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentResponse;
import com.clara.ops.challenge.document_management_service_challenge.entities.Document;
import com.clara.ops.challenge.document_management_service_challenge.exceptions.DatabaseSaveException;
import com.clara.ops.challenge.document_management_service_challenge.mappers.DocumentMapper;
import com.clara.ops.challenge.document_management_service_challenge.repositories.DocumentRepository;
import com.clara.ops.challenge.document_management_service_challenge.specifications.DocumentSpecification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class DocumentService {
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);
  private final DocumentRepository documentRepository;

  public Long saveMetadataDocument(
      DocumentRequest documentRequest, MultipartFile file, String fileUrl) {
    LOGGER.info("DocumentService: Uploading document service...{}", documentRequest);
    Document document = createDocument(documentRequest, file, fileUrl);
    Optional<Document> documentAlreadySaved =
        documentRepository.findByUserNameAndDocumentName(
            document.getUserName(), document.getDocumentName());
    Document documentToSave;

    if (documentAlreadySaved.isPresent()) {
      Document docExisting = documentAlreadySaved.get();

      docExisting.setTags(document.getTags());
      docExisting.setFilePath(document.getFilePath());
      docExisting.setFileSize(document.getFileSize());
      docExisting.setFileType(document.getFileType());

      documentToSave = docExisting;
      LOGGER.info("DocumentService: Existing document found... updating values... ");
    } else {
      documentToSave = document;
      LOGGER.info("DocumentService: No existing document found... creating new one... ");
    }

    Document documentSaved;
    try {
      documentSaved = documentRepository.save(documentToSave);
      if (documentSaved.getId() == null) {
        LOGGER.error("DocumentService: Error saving metadata info to database");
        throw new DatabaseSaveException("Error saving document metadata");
      }
    } catch (Exception e) {
      LOGGER.info("DocumentService: Error saving metadata info to database: {}", e.getMessage());
      throw new DatabaseSaveException(e.getMessage());
    }

    LOGGER.info(
        "DocumentService: Values was inserted successfully with id {}", documentSaved.getId());
    return documentSaved.getId();
  }

  public Page<DocumentResponse> getFilteredDocuments(
      String userName, String documentName, List<String> tags, int page, int size) {
    LOGGER.info("DocumentService: filtering documents ");

    Specification<Document> spec = DocumentSpecification.withFilters(userName, documentName, tags);
    Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
    Pageable pageable = PageRequest.of(page, size, sort);
    return documentRepository.findAll(spec, pageable).map(DocumentMapper::documentEntityToDTO);
  }

  private Document createDocument(DocumentRequest dto, MultipartFile file, String fileUrl) {
    return Document.builder()
        .documentName(dto.getDocumentName())
        .tags(dto.getTags())
        .createdAt(LocalDateTime.now())
        .filePath(fileUrl)
        .fileSize(file.getSize())
        .fileType(file.getContentType())
        .userName(dto.getUserName())
        .build();
  }
}
