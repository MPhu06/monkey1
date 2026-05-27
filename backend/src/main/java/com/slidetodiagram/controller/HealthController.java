package com.slidetodiagram.controller;

import com.slidetodiagram.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "status", "UP",
                "service", "slide-to-diagram-backend",
                "version", "1.0.0"
        )));
    }
}
