package com.slidetodiagram.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class DiagramGeneratorService {

    private static final Map<String, String> DIAGRAM_TYPES = Map.of(
            "flowchart", "flowchart TD",
            "sequence", "sequenceDiagram",
            "mindmap", "mindmap",
            "er", "erDiagram",
            "class", "classDiagram",
            "org", "graph TD"
    );

    private static final String ARROW = "-->";
    private static final String LABEL_ARROW = "-->";
    private static final String CONDITION = "?";
    private static final String SUBGRAPH_OPEN = "subgraph";
    private static final String SUBGRAPH_CLOSE = "end";

    private int nodeCounter = 0;
    private final Map<String, String> nodeMap = new LinkedHashMap<>();

    public String generateMermaidCode(String inputText, String diagramType) {
        nodeCounter = 0;
        nodeMap.clear();

        if (inputText == null || inputText.trim().isEmpty()) {
            return getEmptyDiagram(diagramType);
        }

        return switch (diagramType.toLowerCase()) {
            case "flowchart" -> generateFlowchart(inputText);
            case "sequence" -> generateSequenceDiagram(inputText);
            case "mindmap" -> generateMindMap(inputText);
            case "er" -> generateERDiagram(inputText);
            case "class" -> generateClassDiagram(inputText);
            case "org" -> generateOrgChart(inputText);
            default -> generateFlowchart(inputText);
        };
    }

    private String generateFlowchart(String inputText) {
        StringBuilder mermaid = new StringBuilder();
        mermaid.append("flowchart TD\n");

        String[] lines = splitLines(inputText);
        List<String> nodes = new ArrayList<>();
        String prevNode = null;

        for (String line : lines) {
            String cleaned = cleanLine(line);
            if (cleaned.isEmpty()) continue;

            if (isDecision(cleaned)) {
                String nodeId = "D" + (++nodeCounter);
                nodeMap.put(nodeId, cleaned);
                mermaid.append(String.format("    %s{\"%s\"}\n", nodeId, cleaned));

                if (prevNode != null) {
                    mermaid.append(String.format("    %s %s %s\n", prevNode, ARROW, nodeId));
                }
                prevNode = nodeId;

                handleDecisionBranches(mermaid, cleaned, nodeId);
            } else if (cleaned.contains("-->") || cleaned.contains("->")) {
                handleArrowLine(mermaid, cleaned);
            } else {
                String nodeId = "N" + (++nodeCounter);
                nodeMap.put(nodeId, cleaned);
                mermaid.append(String.format("    %s[\"%s\"]\n", nodeId, cleaned));

                if (prevNode != null) {
                    mermaid.append(String.format("    %s %s %s\n", prevNode, ARROW, nodeId));
                }
                prevNode = nodeId;
            }
            nodes.add(cleaned);
        }

        if (nodeCounter == 0) {
            mermaid.append("    A[\"Start\"]\n");
            mermaid.append("    B[\"No content provided\"]\n");
            mermaid.append("    A --> B\n");
        }

        return mermaid.toString();
    }

    private void handleDecisionBranches(StringBuilder mermaid, String text, String decisionId) {
        String lower = text.toLowerCase();
        if (lower.contains("nếu") || lower.contains("if") || lower.contains("?") || lower.contains("có/không") || lower.contains("yes/no")) {
            String yesId = "Y" + (++nodeCounter);
            String noId = "N" + (++nodeCounter);
            String mergeId = "M" + (++nodeCounter);

            mermaid.append(String.format("    %s -->|Có / Yes| %s[\"Tiếp tục\"]\n", decisionId, yesId));
            mermaid.append(String.format("    %s -->|Không / No| %s[\"Dừng lại\"]\n", decisionId, noId));
            mermaid.append(String.format("    %s --> %s\n", yesId, mergeId));
            mermaid.append(String.format("    %s --> %s\n", noId, mergeId));
        }
    }

    private void handleArrowLine(StringBuilder mermaid, String line) {
        String separator = line.contains("-->") ? "-->" : "->";
        String[] parts = line.split(Pattern.quote(separator));

        if (parts.length >= 2) {
            String from = cleanLine(parts[0]);
            String to = cleanLine(parts[1]);
            String fromId = getOrCreateNode(from, mermaid, false);
            String toId = getOrCreateNode(to, mermaid, true);
            mermaid.append(String.format("    %s %s %s\n", fromId, ARROW, toId));
        }
    }

    private String getOrCreateNode(String label, StringBuilder mermaid, boolean autoAdd) {
        for (Map.Entry<String, String> entry : nodeMap.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(label)) {
                return entry.getKey();
            }
        }
        if (autoAdd) {
            String nodeId = "N" + (++nodeCounter);
            nodeMap.put(nodeId, label);
            mermaid.append(String.format("    %s[\"%s\"]\n", nodeId, label));
            return nodeId;
        }
        return "N" + (++nodeCounter);
    }

    private boolean isDecision(String text) {
        String lower = text.toLowerCase();
        return lower.contains("nếu") || lower.contains("if") || lower.contains("?") ||
                lower.contains("có hay không") || lower.contains("đúng sai") ||
                lower.contains("điều kiện") || lower.contains("condition") ||
                text.contains("[") && text.contains("]") && text.length() < 50;
    }

    private String generateSequenceDiagram(String inputText) {
        StringBuilder mermaid = new StringBuilder();
        mermaid.append("sequenceDiagram\n");

        String[] lines = splitLines(inputText);
        String currentActor = "Người dùng";
        boolean actorDeclared = false;

        Set<String> actors = new LinkedHashSet<>();
        actors.add("Người dùng");
        List<String> messages = new ArrayList<>();

        for (String line : lines) {
            String cleaned = cleanLine(line);
            if (cleaned.isEmpty()) continue;

            if (cleaned.startsWith("actor:") || cleaned.startsWith("người dùng:") ||
                    cleaned.startsWith("hệ thống:") || cleaned.startsWith("server:") ||
                    cleaned.startsWith("client:")) {
                int colonIdx = cleaned.indexOf(':');
                if (colonIdx > 0) {
                    String actor = capitalize(cleaned.substring(0, colonIdx).replace("actor:", "").trim());
                    String message = cleaned.substring(colonIdx + 1).trim();
                    if (!actors.contains(actor)) {
                        actors.add(actor);
                        mermaid.append(String.format("    participant %s\n", actor));
                    }
                    messages.add(String.format("    Người dùng %s %s: %s",
                            LABEL_ARROW, actor, message));
                }
            } else if (cleaned.contains("-->") || cleaned.contains("->")) {
                String[] parts = cleaned.split("-->|->");
                if (parts.length >= 2) {
                    String from = capitalize(cleanLine(parts[0]));
                    String to = capitalize(cleanLine(parts[1]));
                    if (!actors.contains(from)) {
                        actors.add(from);
                        mermaid.append(String.format("    participant %s\n", from));
                    }
                    if (!actors.contains(to)) {
                        actors.add(to);
                        mermaid.append(String.format("    participant %s\n", to));
                    }
                    messages.add(String.format("    %s %s %s", from, ARROW, to));
                }
            } else {
                messages.add(String.format("    %s %s Hệ thống: %s", currentActor, ARROW, cleaned));
            }
        }

        for (String actor : actors) {
            if (!actor.equals("Người dùng")) {
                mermaid.append(String.format("    participant %s\n", actor));
            }
        }
        mermaid.append("\n");
        for (String msg : messages) {
            mermaid.append(msg).append("\n");
        }

        if (messages.isEmpty()) {
            mermaid.append("    Người dùng->>Hệ thống: Yêu cầu\n");
            mermaid.append("    Hệ thống-->>Người dùng: Phản hồi\n");
        }

        return mermaid.toString();
    }

    private String generateMindMap(String inputText) {
        StringBuilder mermaid = new StringBuilder();
        mermaid.append("mindmap\n");

        String[] lines = splitLines(inputText);
        String root = "Nội dung chính";

        for (String line : lines) {
            String cleaned = cleanLine(line);
            if (cleaned.isEmpty()) continue;

            if (cleaned.startsWith("#") || cleaned.startsWith("- ")) {
                cleaned = cleaned.replaceAll("^[#\\-\\*\\s]+", "");
            }
            if (nodeCounter == 0) {
                root = cleaned;
            } else {
                String[] words = cleaned.split("[,\\-\\|]+");
                for (String word : words) {
                    String w = word.trim();
                    if (!w.isEmpty()) {
                        mermaid.append(String.format("    %s %s\n",
                                getMindMapBullet(nodeCounter), w));
                    }
                }
            }
            nodeCounter++;
        }

        if (nodeCounter == 0) {
            mermaid.append("    root((Nội dung chính))\n");
            mermaid.append("        Idea 1\n");
            mermaid.append("        Idea 2\n");
            mermaid.append("        Idea 3\n");
        }

        return mermaid.toString();
    }

    private String getMindMapBullet(int level) {
        return switch (level % 3) {
            case 0 -> "  ";
            case 1 -> "    ";
            default -> "      ";
        };
    }

    private String generateERDiagram(String inputText) {
        StringBuilder mermaid = new StringBuilder();
        mermaid.append("erDiagram\n");

        String[] lines = splitLines(inputText);
        String currentEntity = null;
        List<String> currentFields = new ArrayList<>();

        for (String line : lines) {
            String cleaned = cleanLine(line);
            if (cleaned.isEmpty()) continue;

            if (cleaned.startsWith("table:") || cleaned.startsWith("bảng:") ||
                    cleaned.startsWith("entity:") || cleaned.startsWith("thực thể:")) {
                if (currentEntity != null && !currentFields.isEmpty()) {
                    mermaid.append(String.format("    %s %s\n", currentEntity, "{");
                    for (String field : currentFields) {
                        String[] parts = field.split(":");
                        String fieldName = parts[0].trim();
                        String fieldType = parts.length > 1 ? parts[1].trim() : "string";
                        mermaid.append(String.format("        %s %s\n", fieldName, fieldType));
                    }
                    mermaid.append("    }\n");
                }
                currentEntity = cleaned.replaceAll("^(table:|bảng:|entity:|thực thể:)\\s*", "").trim();
                currentFields = new ArrayList<>();
            } else if (currentEntity != null) {
                currentFields.add(cleaned);
            } else {
                String[] words = cleaned.split("[,\\s]+");
                if (words.length >= 2) {
                    currentEntity = words[0];
                    for (int i = 1; i < words.length; i++) {
                        currentFields.add(words[i] + " : string");
                    }
                }
            }
        }

        if (currentEntity != null && !currentFields.isEmpty()) {
            mermaid.append(String.format("    %s %s\n", currentEntity, "{"));
            for (String field : currentFields) {
                String[] parts = field.split(":");
                String fieldName = parts[0].trim();
                String fieldType = parts.length > 1 ? parts[1].trim() : "string";
                mermaid.append(String.format("        %s %s\n", fieldName, fieldType));
            }
            mermaid.append("    }\n");
        }

        if (currentEntity == null) {
            mermaid.append("    Users {\n");
            mermaid.append("        uuid id PK\n");
            mermaid.append("        string name\n");
            mermaid.append("        string email\n");
            mermaid.append("    }\n");
            mermaid.append("    Orders {\n");
            mermaid.append("        uuid id PK\n");
            mermaid.append("        uuid user_id FK\n");
            mermaid.append("        decimal total\n");
            mermaid.append("    }\n");
        }

        return mermaid.toString();
    }

    private String generateClassDiagram(String inputText) {
        StringBuilder mermaid = new StringBuilder();
        mermaid.append("classDiagram\n");

        String[] lines = splitLines(inputText);
        String currentClass = null;
        List<String> members = new ArrayList<>();

        for (String line : lines) {
            String cleaned = cleanLine(line);
            if (cleaned.isEmpty()) continue;

            if (cleaned.startsWith("class:") || cleaned.startsWith("lớp:")) {
                if (currentClass != null && !members.isEmpty()) {
                    mermaid.append(String.format("    class %s {\n", currentClass));
                    for (String member : members) {
                        mermaid.append(String.format("        %s\n", member));
                    }
                    mermaid.append("    }\n\n");
                }
                currentClass = cleaned.replaceAll("^(class:|lớp:)\\s*", "").trim();
                members = new ArrayList<>();
            } else if (currentClass != null) {
                members.add(cleaned);
            } else {
                String[] words = cleaned.split("[,\\s]+");
                if (words.length >= 2) {
                    currentClass = words[0];
                    for (int i = 1; i < Math.min(words.length, 4); i++) {
                        members.add("+ " + words[i] + " : string");
                    }
                }
            }
        }

        if (currentClass != null && !members.isEmpty()) {
            mermaid.append(String.format("    class %s {\n", currentClass));
            for (String member : members) {
                mermaid.append(String.format("        %s\n", member));
            }
            mermaid.append("    }\n");
        }

        if (currentClass == null) {
            mermaid.append("    class User {\n");
            mermaid.append("        +String name\n");
            mermaid.append("        +String email\n");
            mermaid.append("        +login()\n");
            mermaid.append("        +logout()\n");
            mermaid.append("    }\n");
            mermaid.append("    class Order {\n");
            mermaid.append("        +Double total\n");
            mermaid.append("        +createOrder()\n");
            mermaid.append("    }\n");
        }

        return mermaid.toString();
    }

    private String generateOrgChart(String inputText) {
        StringBuilder mermaid = new StringBuilder();
        mermaid.append("graph TD\n");

        String[] lines = splitLines(inputText);
        String prevNode = null;
        String rootNode = null;

        for (String line : lines) {
            String cleaned = cleanLine(line);
            if (cleaned.isEmpty()) continue;

            String prefix = "    ";
            String nodeId = "O" + (++nodeCounter);

            if (cleaned.startsWith("  ") || cleaned.startsWith("\t")) {
                prefix = "        ";
            }

            if (rootNode == null) {
                rootNode = nodeId;
                mermaid.append(String.format("%s%s(\"%s\")\n", prefix, nodeId, cleaned));
            } else {
                mermaid.append(String.format("%s%s[\"%s\"]\n", prefix, nodeId, cleaned));
                if (prevNode != null) {
                    mermaid.append(String.format("%s%s %s %s\n", prefix, prevNode, ARROW, nodeId));
                }
            }
            prevNode = nodeId;
        }

        if (rootNode == null) {
            mermaid.append("    CEO[\"CEO\"]\n");
            mermaid.append("    CTO[\"CTO\"]\n");
            mermaid.append("    CFO[\"CFO\"]\n");
            mermaid.append("    CEO --> CTO\n");
            mermaid.append("    CEO --> CFO\n");
        }

        return mermaid.toString();
    }

    private String getEmptyDiagram(String type) {
        return switch (type.toLowerCase()) {
            case "flowchart" -> "flowchart TD\n    A[\"No content\"]\n    B[\"Enter text or upload slide\"]\n    A --> B";
            case "sequence" -> "sequenceDiagram\n    Người dùng->>Hệ thống: Yêu cầu\n    Hệ thống-->>Người dùng: Phản hồi";
            case "mindmap" -> "mindmap\n    root((Root))\n        Topic 1\n        Topic 2\n        Topic 3";
            case "er" -> "erDiagram\n    Entity {\n        string name\n        int id\n    }";
            case "class" -> "classDiagram\n    class Example {\n        +String name\n        +doSomething()\n    }";
            case "org" -> "graph TD\n    A[\"CEO\"]\n    B[\"Manager\"]\n    A --> B";
            default -> "flowchart TD\n    A[\"Start\"]\n    B[\"End\"]\n    A --> B";
        };
    }

    private String[] splitLines(String text) {
        return text.split("\\n|\\\\n|;" );
    }

    private String cleanLine(String line) {
        return line.trim()
                .replaceAll("^[\\d\\.\\-\\*\\>\\s]+", "")
                .replaceAll("[\\[\\]{}()]", "")
                .trim();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
