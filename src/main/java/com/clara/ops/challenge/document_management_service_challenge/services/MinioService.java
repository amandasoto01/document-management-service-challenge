package com.clara.ops.challenge.document_management_service_challenge.services;

import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentRequest;
import io.minio.*;
import io.minio.http.Method;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MinioService {
  private static final Logger LOGGER = LoggerFactory.getLogger(MinioService.class);
  private final MinioClient minioClient;

  @Value("${minio.bucket-name}")
  private String bucketName;

  @Async("uploadExecutor")
  public CompletableFuture<String> uploadDocument(
      DocumentRequest documentRequest, MultipartFile file) {
    LOGGER.info("MinioService: Running in thread {}", Thread.currentThread().getName());

    String fileName = documentRequest.getUserName() + "/" + documentRequest.getDocumentName();

    if (!bucketExists(bucketName)) {
      createBucketIfNotExists(bucketName);
    } else {
      LOGGER.info("MinioService: Bucket '{}' already exists.", bucketName);
    }

    try (InputStream inputStream = file.getInputStream()) {
      minioClient.putObject(
          PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                  inputStream, file.getSize(), 5 * 1024 * 1024)
              .contentType(file.getContentType())
              .build());

      LOGGER.info("MinioService: File was uploaded successfully: {}", fileName);
      return CompletableFuture.completedFuture(fileName);
    } catch (Exception e) {
      LOGGER.error("MinioService: There was an error updating the file {}", e.getMessage());
      return CompletableFuture.failedFuture(e);
    }
  }

  public String getDownloadUrl(String fileName) {
    LOGGER.info("MinioService: getDownloadUrl method, fileName {}", fileName);
    try {
      return minioClient.getPresignedObjectUrl(
          GetPresignedObjectUrlArgs.builder()
              .bucket(bucketName)
              .object(fileName)
              .method(Method.GET)
              .expiry(300)
              .build());
    } catch (Exception e) {
      LOGGER.info("MinioService: Error getting download link");
      throw new RuntimeException(e);
    }
  }

  private void createBucketIfNotExists(String bucketName) {
    try {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
      LOGGER.info("MinioService: Bucket '{}' created successfully.", bucketName);
    } catch (Exception e) {
      LOGGER.error("MinioService: Error creating bucket {}", e.getMessage());
    }
  }

  private boolean bucketExists(String bucketName) {
    try {
      return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    } catch (Exception e) {
        LOGGER.error("MinioService: Error checking bucket existence: {}", e.getMessage());
      return false;
    }
  }
}
