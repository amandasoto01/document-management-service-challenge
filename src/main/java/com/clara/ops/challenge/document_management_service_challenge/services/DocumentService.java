package com.clara.ops.challenge.document_management_service_challenge.services;

import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentUploadRequest;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

  public Long uploadDocument(
      DocumentUploadRequest documentUploadRequest, MultipartFile file, String fileUrl);
}
