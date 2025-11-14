package com.clara.ops.challenge.document_management_service_challenge.services;

import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentRequest;
import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

  public Long saveMetadataDocument(
      DocumentRequest documentRequest, MultipartFile file, String fileUrl);

  public Page<DocumentResponse> getFilteredDocuments(
      String userName, String documentName, int page, int size);
}
