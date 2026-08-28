package ai.startupforge.core.export;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/blueprints")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/{blueprintId}/export")
    public ResponseEntity<String> export(
            @PathVariable UUID blueprintId,
            @RequestParam(defaultValue = "MARKDOWN") ExportFormat format
    ) {
        String result = exportService.export(blueprintId, format);

        MediaType mediaType = switch (format) {
            case JSON -> MediaType.APPLICATION_JSON;
            case MARKDOWN, PDF -> MediaType.TEXT_PLAIN;
        };

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename="startup-blueprint."
                                + format.name().toLowerCase() + """
                )
                .contentType(mediaType)
                .body(result);
    }
}
