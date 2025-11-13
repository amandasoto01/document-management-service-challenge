package com.clara.ops.challenge.document_management_service_challenge.exceptions;

public class FileSizeLimitException extends RuntimeException {

  public FileSizeLimitException(String message) {
    super(message);
  }
}
