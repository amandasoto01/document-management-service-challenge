package com.clara.ops.challenge.document_management_service_challenge.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "documents", schema = "document_schema")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Document {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "document_id")
  private Long id;

  @Column(name = "user_name", nullable = false)
  private String userName;

  @Column(name = "document_name", nullable = false)
  private String documentName;

  private String[] tags;

  @Column(name = "file_path", nullable = false)
  private String filePath;

  @Column(name = "file_size")
  private Long fileSize;

  @Column(name = "file_type")
  private String fileType;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}
