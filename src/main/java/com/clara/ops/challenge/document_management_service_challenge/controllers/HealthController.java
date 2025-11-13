package com.clara.ops.challenge.document_management_service_challenge.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/document/health")
public class HealthController {

  @GetMapping
  public ResponseEntity<String> healthCheck() {
    return ResponseEntity.ok("Document Management Service is Up and Running");
  }
}
