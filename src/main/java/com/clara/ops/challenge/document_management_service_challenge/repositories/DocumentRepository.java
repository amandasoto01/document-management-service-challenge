package com.clara.ops.challenge.document_management_service_challenge.repositories;

import com.clara.ops.challenge.document_management_service_challenge.entities.Document;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentRepository
    extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {
  Optional<Document> findByUserNameAndDocumentName(String userName, String documentName);
}
