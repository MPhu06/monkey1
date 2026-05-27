package com.slidetodiagram.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "diagram_jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagramJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "input_type", nullable = false, length = 20)
    private String inputType;

    @Column(name = "input_text", columnDefinition = "TEXT")
    private String inputText;

    @Column(name = "diagram_type", nullable = false, length = 30)
    private String diagramType;

    @Column(name = "mermaid_code", columnDefinition = "TEXT")
    private String mermaidCode;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "pending";

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
