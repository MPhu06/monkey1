package com.slidetodiagram.service;

import com.slidetodiagram.dto.DiagramResponse;
import com.slidetodiagram.dto.GenerateDiagramRequest;
import com.slidetodiagram.model.DiagramJob;
import com.slidetodiagram.repository.DiagramJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiagramService {

    private final DiagramJobRepository diagramJobRepository;
    private final DiagramGeneratorService diagramGeneratorService;
    private final PptxParserService pptxParserService;

    @Transactional
    public DiagramResponse generateDiagram(GenerateDiagramRequest request, MultipartFile file) {
        String inputText = request.getInputText();

        if ("pptx".equalsIgnoreCase(request.getInputType()) && file != null && !file.isEmpty()) {
            try {
                inputText = pptxParserService.parsePptx(file);
            } catch (Exception e) {
                log.error("Error parsing PPTX: {}", e.getMessage());
                throw new RuntimeException("Failed to parse PPTX file: " + e.getMessage());
            }
        }

        if (inputText == null || inputText.trim().isEmpty()) {
            throw new IllegalArgumentException("Input text cannot be empty");
        }

        String mermaidCode = diagramGeneratorService.generateMermaidCode(
                inputText,
                request.getDiagramType()
        );

        DiagramJob job = DiagramJob.builder()
                .inputType(request.getInputType())
                .inputText(inputText.length() > 2000 ? inputText.substring(0, 2000) : inputText)
                .diagramType(request.getDiagramType())
                .mermaidCode(mermaidCode)
                .imageUrl("/api/diagrams/" + UUID.randomUUID() + "/image")
                .status("completed")
                .build();

        DiagramJob saved = diagramJobRepository.save(job);
        return mapToResponse(saved);
    }

    @Transactional
    public DiagramResponse generateDiagramFromText(GenerateDiagramRequest request) {
        return generateDiagram(request, null);
    }

    public List<DiagramResponse> getAllDiagrams() {
        return diagramJobRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DiagramResponse getDiagramById(UUID id) {
        DiagramJob job = diagramJobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diagram not found with id: " + id));
        return mapToResponse(job);
    }

    @Transactional
    public void deleteDiagram(UUID id) {
        if (!diagramJobRepository.existsById(id)) {
            throw new RuntimeException("Diagram not found with id: " + id);
        }
        diagramJobRepository.deleteById(id);
    }

    private DiagramResponse mapToResponse(DiagramJob job) {
        return DiagramResponse.builder()
                .id(job.getId())
                .inputType(job.getInputType())
                .inputText(job.getInputText())
                .diagramType(job.getDiagramType())
                .mermaidCode(job.getMermaidCode())
                .imageUrl(job.getImageUrl())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
