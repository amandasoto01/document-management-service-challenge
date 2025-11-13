package com.clara.ops.challenge.document_management_service_challenge.services;

import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentResponse;
import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentUploadRequest;
import com.clara.ops.challenge.document_management_service_challenge.entities.Document;
import com.clara.ops.challenge.document_management_service_challenge.mappers.DocumentMapper;
import com.clara.ops.challenge.document_management_service_challenge.repositories.DocumentRepository;
import com.clara.ops.challenge.document_management_service_challenge.specifications.DocumentSpecification;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class DocumentServiceImpl implements DocumentService {
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentServiceImpl.class);
  @Autowired private final DocumentRepository documentRepository;

  @Override
  public Long uploadDocument(
      DocumentUploadRequest documentUploadRequest, MultipartFile file, String fileUrl) {
    LOGGER.info("DocumentService: Uploading document service...");
    Document document = createDocument(documentUploadRequest, file, fileUrl);
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
      LOGGER.info("Existing document found... updating values... ");
    } else {
      documentToSave = document;
      LOGGER.info("No existing document found... creating new one... ");
    }

    Document documentSaved = documentRepository.save(documentToSave);
    if (documentSaved.getId() == null) {
      LOGGER.error("Error saving document info");
      return -1L;
    }
    LOGGER.info("Values was inserted successfully with id {}", documentSaved.getId());
    return documentSaved.getId();
  }

  @Override
  public Page<DocumentResponse> getFilteredDocuments(
      String userName, String documentName, int page, int size) {
    LOGGER.info("DocumentService: filtering documents ");

    Specification<Document> spec = DocumentSpecification.withFilters(userName, documentName);
    Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
    Pageable pageable = PageRequest.of(page, size, sort);
    return documentRepository.findAll(spec, pageable).map(DocumentMapper::documentEntityToDTO);
  }

  private Document createDocument(DocumentUploadRequest dto, MultipartFile file, String fileUrl) {
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
