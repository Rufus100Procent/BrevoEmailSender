package se.stykle.brevoemailsender.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.stykle.brevoemailsender.BrevoTemplate;
import se.stykle.brevoemailsender.service.TemplateService;
import software.xdev.brevo.model.CreateModel;
import software.xdev.brevo.model.GetSmtpTemplates;

@RequestMapping("/api/v1")
@RestController
@CrossOrigin("*")
public class TemplateController {
    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> listTemplates(
            @RequestParam(defaultValue = "true") boolean isActive,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("API-Version", "1.0.0");

        GetSmtpTemplates templates = templateService.fetchAllTemplates(isActive, limit, offset);
        return templates != null ? ResponseEntity.ok(templates) :
                ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .headers(responseHeaders)
                        .body("Error fetching templates");
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTemplate(@RequestBody BrevoTemplate request) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("API-Version", "1.0.0");

        CreateModel response = templateService.createCustomTemplate(request);
        return response != null ? ResponseEntity.ok(response) :
                ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .headers(responseHeaders)
                        .body("Error creating template");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateTemplate(
            @RequestParam Long templateId,
            @RequestBody BrevoTemplate request) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("API-Version", "1.0.0");

        try {
            templateService.updateTemplate(templateId, request);
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body("Template updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(responseHeaders)
                    .body("Failed to update template: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteTemplate(@RequestParam Long templateId) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("API-Version", "1.0.0");

        boolean success = templateService.deleteTemplate(templateId);
        if (success) {
            return ResponseEntity.ok("Template deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(responseHeaders)
                    .body("Error deleting template or template not found.");
        }
    }

    @PutMapping("/deactivate")
    public ResponseEntity<String> deactivateTemplate(@RequestParam Long templateId) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("API-Version", "1.0.0");

        boolean success = templateService.deactivateTemplate(templateId);
        return success ? ResponseEntity.ok("Template deactivated successfully!") :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .headers(responseHeaders)
                        .body("Failed to deactivate template.");
    }

    @PutMapping("/activate")
    public ResponseEntity<String> activateTemplate(@RequestParam Long templateId) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("API-Version", "1.0.0");

        boolean success = templateService.activateTemplate(templateId);
        return success ? ResponseEntity.ok("Template activated successfully!") :
                ResponseEntity.
                        status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .headers(responseHeaders)
                        .body("Failed to activate template.");
    }

}
