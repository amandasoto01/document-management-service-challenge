package com.clara.ops.challenge.document_management_service_challenge.utils;

import com.clara.ops.challenge.document_management_service_challenge.exceptions.FileSizeLimitException;
import com.clara.ops.challenge.document_management_service_challenge.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

  private static final long MAX_SIZE = 500L * 1024 * 1024; // 500MB

  public static void validateFile(MultipartFile file) {
    LOGGER.info("Validating file");
    if (file.isEmpty()) {
      throw new InvalidFormatException("File is required");
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
      throw new InvalidFormatException("Invalid format, valid format is PDF");
    }

    if (file.getSize() > MAX_SIZE) {
      throw new FileSizeLimitException("File too large, max 500MB");
    }
  }
}
