package com.slidetodiagram.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PptxParserService {

    public String parsePptx(MultipartFile file) {
        try (XMLSlideShow pptx = new XMLSlideShow(new ByteArrayInputStream(file.getBytes()))) {
            List<String> slideContents = new ArrayList<>();

            for (XSLFSlide slide : pptx.getSlides()) {
                StringBuilder slideText = new StringBuilder();

                for (XSLFShape shape : slide.getShapes()) {
                    if (shape instanceof XSLFTextShape textShape) {
                        for (XSLFTextParagraph paragraph : textShape.getTextParagraphs()) {
                            StringBuilder paraText = new StringBuilder();
                            for (XSLFTextRun run : paragraph.getTextRuns()) {
                                paraText.append(run.getRawText());
                            }
                            if (!paraText.isEmpty()) {
                                slideText.append(paraText).append("\n");
                            }
                        }
                    }
                }

                if (!slideText.isEmpty()) {
                    slideContents.add("=== Slide ===\n" + slideText);
                }
            }

            if (slideContents.isEmpty()) {
                return "No text content found in the presentation.";
            }

            return String.join("\n", slideContents);

        } catch (IOException e) {
            log.error("Error parsing PPTX file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse PPTX file: " + e.getMessage());
        }
    }
}
