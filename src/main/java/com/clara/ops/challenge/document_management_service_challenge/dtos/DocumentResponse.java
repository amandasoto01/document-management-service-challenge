package com.clara.ops.challenge.document_management_service_challenge.dtos;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponse {
  private Long id;
  private String userName;
  private String documentName;
  private List<String> tags;
  private Long fileSize;
  private String fileType;
  private String filePath;
  private LocalDateTime createdAt;
}
