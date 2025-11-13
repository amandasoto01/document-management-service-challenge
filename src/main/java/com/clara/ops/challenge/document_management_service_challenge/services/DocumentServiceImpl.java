package com.clara.ops.challenge.document_management_service_challenge.services;

import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentUploadRequest;
import com.clara.ops.challenge.document_management_service_challenge.entities.Document;
import com.clara.ops.challenge.document_management_service_challenge.repositories.DocumentRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    LOGGER.info("Uploading document service...");
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
