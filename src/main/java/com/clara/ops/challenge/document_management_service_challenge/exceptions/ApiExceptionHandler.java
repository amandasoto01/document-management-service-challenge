package com.clara.ops.challenge.document_management_service_challenge.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ApiExceptionHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({Exception.class})
  @ResponseBody
  public ResponseEntity<ApiError> handleGeneralException(HttpServletRequest request, Exception ex) {
    LOGGER.error(ex.getMessage());
    return ResponseEntity.internalServerError().body(new ApiError(ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationException(
      HttpServletRequest request, MethodArgumentNotValidException ex) {
    String errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .toList()
            .stream()
            .map(String::valueOf)
            .collect(Collectors.joining(", "));
    LOGGER.error(errors);
    return ResponseEntity.badRequest().body(new ApiError(errors));
  }

  @ExceptionHandler({FileSizeLimitException.class, MaxUploadSizeExceededException.class})
  public ResponseEntity<ApiError> handleFileSizeLimitException(FileSizeLimitException ex) {
    LOGGER.error(ex.getMessage());
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new ApiError(ex.getMessage()));
  }

  @ExceptionHandler(InvalidFormatException.class)
  public ResponseEntity<ApiError> handleInvalidFormatException(InvalidFormatException ex) {
    LOGGER.error(ex.getMessage());
    return ResponseEntity.badRequest().body(new ApiError("Invalid format " + ex.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
    LOGGER.error(ex.getMessage());
    return ResponseEntity.badRequest().body(new ApiError("Illegal Argument " + ex.getMessage()));
  }

  @ExceptionHandler(InvalidDocumentIdException.class)
  public ResponseEntity<ApiError> handleInvalidDocumentException(InvalidDocumentIdException ex) {
    LOGGER.error(ex.getMessage());
    return ResponseEntity.badRequest().body(new ApiError("Invalid document id " + ex.getMessage()));
  }

  @ExceptionHandler(DatabaseSaveException.class)
  public ResponseEntity<ApiError> handleDatabaseSaveException(DatabaseSaveException ex) {
    LOGGER.error(ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiError("Database save error: " + ex.getMessage()));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiError> handleDataIntegrityValidationException(
      DataIntegrityViolationException ex) {
    LOGGER.error(ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiError("Data integrity violation: " + ex.getMessage()));
  }
}
