package com.clara.ops.challenge.document_management_service_challenge.exceptions;

public class InvalidDocumentIdException extends RuntimeException {
  public InvalidDocumentIdException(String message) {
    super(message);
  }
}
