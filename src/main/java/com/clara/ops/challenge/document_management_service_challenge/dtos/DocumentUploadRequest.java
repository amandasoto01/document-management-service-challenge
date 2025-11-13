package com.clara.ops.challenge.document_management_service_challenge.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DocumentUploadRequest {

  @NotNull(message = "User name is required") @NotBlank(message = "User name must not be blank")
  private String userName;

  private String documentName;

  private String[] tags;
}
