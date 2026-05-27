package com.slidetodiagram.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateDiagramRequest {

    @NotNull(message = "Input type is required")
    private String inputType;

    private String inputText;

    @NotBlank(message = "Diagram type is required")
    private String diagramType;
}
