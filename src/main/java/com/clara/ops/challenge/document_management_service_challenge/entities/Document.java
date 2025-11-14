package com.clara.ops.challenge.document_management_service_challenge.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
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

  @ElementCollection
  @CollectionTable(
      name = "document_tags",
      schema = "document_schema",
      joinColumns = @JoinColumn(name = "document_id"))
  @Column(name = "tag")
  private List<String> tags;

  @Column(name = "file_path", nullable = false)
  private String filePath;

  @Column(name = "file_size")
  private Long fileSize;

  @Column(name = "file_type")
  private String fileType;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}
