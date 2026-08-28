package ai.startupforge.core.export;

import ai.startupforge.core.blueprint.Blueprint;
import ai.startupforge.core.blueprint.BlueprintRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExportService {

    private final BlueprintRepository blueprintRepository;

    public ExportService(BlueprintRepository blueprintRepository) {
        this.blueprintRepository = blueprintRepository;
    }

    public String export(UUID blueprintId, ExportFormat format) {
        Blueprint blueprint = blueprintRepository.findById(blueprintId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Blueprint not found: " + blueprintId
                ));

        return switch (format) {
            case JSON -> toJson(blueprint);
            case MARKDOWN -> toMarkdown(blueprint);
            case PDF -> "PDF export prepared for blueprint " + blueprint.getId();
        };
    }

    private String toJson(Blueprint blueprint) {
        return """
                {
                  "id": "%s",
                  "projectId": "%s",
                  "generationId": "%s",
                  "title": "%s",
                  "executiveSummary": "%s",
                  "content": "%s"
                }
                """.formatted(
                blueprint.getId(),
                blueprint.getProjectId(),
                blueprint.getGenerationId(),
                escape(blueprint.getTitle()),
                escape(blueprint.getExecutiveSummary()),
                escape(blueprint.getContent())
        );
    }

    private String toMarkdown(Blueprint blueprint) {
        return """
                # %s

                ## Executive Summary

                %s

                ## Startup Blueprint

                %s
                """.formatted(
                nullSafe(blueprint.getTitle()),
                nullSafe(blueprint.getExecutiveSummary()),
                nullSafe(blueprint.getContent())
        );
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace(""", "\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
