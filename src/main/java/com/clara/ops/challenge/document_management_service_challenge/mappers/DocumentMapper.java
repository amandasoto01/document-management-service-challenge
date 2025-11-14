package com.clara.ops.challenge.document_management_service_challenge.mappers;

import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentResponse;
import com.clara.ops.challenge.document_management_service_challenge.entities.Document;

public final class DocumentMapper {

  private DocumentMapper() {}

  public static DocumentResponse documentEntityToDTO(Document document) {
    return DocumentResponse.builder()
        .id(document.getId())
        .userName(document.getUserName())
        .documentName(document.getDocumentName())
        .tags(document.getTags())
        .filePath(document.getFilePath())
        .fileSize(document.getFileSize())
        .fileType(document.getFileType())
        .createdAt(document.getCreatedAt())
        .build();
  }
}
