package com.clara.ops.challenge.document_management_service_challenge.exceptions;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ApiError {
  private final LocalDateTime dateTime;
  private final String errorMessage;

  public ApiError(String message) {
    this.errorMessage = message;
    this.dateTime = LocalDateTime.now();
  }

  public ApiError(Exception exception) {
    this(exception.getMessage());
  }
}
