package com.clara.ops.challenge.document_management_service_challenge.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.clara.ops.challenge.document_management_service_challenge.dtos.DocumentRequest;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

  @Mock private MinioClient minioClient;

  @InjectMocks private MinioService minioService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(minioService, "bucketName", "test-bucket");
  }

  @Test
  void uploadDocument_success_shouldCallMinioPutObject() throws Exception {
    DocumentRequest request = new DocumentRequest("doc1", "user1", List.of("tag1"));

    MockMultipartFile file =
        new MockMultipartFile("file", "test.pdf", "application/pdf", "Test Content".getBytes());

    minioService.uploadDocument(request, file);

    verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
  }

  @Test
  void uploadDocument_whenMinioThrows_shouldPropagateOrWrapException() throws Exception {
    DocumentRequest request = new DocumentRequest("doc1", "user1", List.of("tag1"));
    MockMultipartFile file =
        new MockMultipartFile("file", "test.pdf", "application/pdf", "Test Content".getBytes());

    doThrow(new RuntimeException("Minio error"))
        .when(minioClient)
        .putObject(any(PutObjectArgs.class));

    CompletableFuture<String> result = minioService.uploadDocument(request, file);

    assertTrue(result.isCompletedExceptionally());

    assertThrows(RuntimeException.class, result::join);
  }

  @Test
  void getDownloadUrl_success_shouldReturnUrl() throws Exception {
    String fileName = "test.pdf";
    String expectedUrl = "https://minio.test-bucket/test.pdf";

    when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
        .thenReturn(expectedUrl);

    String result = minioService.getDownloadUrl(fileName);

    assertEquals(expectedUrl, result);
    verify(minioClient, times(1)).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
  }

  @Test
  void getDownloadUrl_whenMinioThrows_shouldPropagateOrWrapException() throws Exception {
    when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
        .thenThrow(new RuntimeException("Minio error"));

    assertThrows(RuntimeException.class, () -> minioService.getDownloadUrl("test.pdf"));
  }
}
