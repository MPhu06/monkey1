package com.slidetodiagram.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileUploadController {

    @PostMapping(value = "/upload-pptx", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadPptx(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "No file uploaded"
            ));
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pptx")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Only .pptx files are supported"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "filename", filename,
                "size", file.getSize()
        ));
    }
}
