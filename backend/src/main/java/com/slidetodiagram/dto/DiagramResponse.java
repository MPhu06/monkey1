package com.slidetodiagram.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagramResponse {

    private UUID id;
    private String inputType;
    private String inputText;
    private String diagramType;
    private String mermaidCode;
    private String imageUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
