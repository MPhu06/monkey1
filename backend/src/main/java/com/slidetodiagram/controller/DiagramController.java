package com.slidetodiagram.controller;

import com.slidetodiagram.dto.ApiResponse;
import com.slidetodiagram.dto.DiagramResponse;
import com.slidetodiagram.dto.GenerateDiagramRequest;
import com.slidetodiagram.service.DiagramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/diagrams")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DiagramController {

    private final DiagramService diagramService;

    @PostMapping(value = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DiagramResponse>> generateDiagram(
            @RequestParam("inputType") String inputType,
            @RequestParam(value = "inputText", required = false) String inputText,
            @RequestParam("diagramType") String diagramType,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            GenerateDiagramRequest request = GenerateDiagramRequest.builder()
                    .inputType(inputType)
                    .inputText(inputText)
                    .diagramType(diagramType)
                    .build();

            DiagramResponse response;
            if ("pptx".equalsIgnoreCase(inputType) && file != null && !file.isEmpty()) {
                response = diagramService.generateDiagram(request, file);
            } else {
                response = diagramService.generateDiagramFromText(request);
            }

            return ResponseEntity.ok(ApiResponse.success("Diagram generated successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to generate diagram: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DiagramResponse>>> getAllDiagrams() {
        List<DiagramResponse> diagrams = diagramService.getAllDiagrams();
        return ResponseEntity.ok(ApiResponse.success(diagrams));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DiagramResponse>> getDiagramById(@PathVariable UUID id) {
        try {
            DiagramResponse diagram = diagramService.getDiagramById(id);
            return ResponseEntity.ok(ApiResponse.success(diagram));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDiagram(@PathVariable UUID id) {
        try {
            diagramService.deleteDiagram(id);
            return ResponseEntity.ok(ApiResponse.success("Diagram deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
