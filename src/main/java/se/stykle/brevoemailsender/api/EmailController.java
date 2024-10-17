package se.stykle.brevoemailsender.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.stykle.brevoemailsender.entity.EmailData;
import se.stykle.brevoemailsender.entity.dto.EmailSummaryDTO;
import se.stykle.brevoemailsender.service.EmailService;
import software.xdev.brevo.model.GetScheduledEmailById200Response;

import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(
            @RequestParam(required = false) Long templateId,
            @RequestBody EmailData emailData,
            @RequestParam(required = false) String scheduledAt,
            @RequestParam(required = false) String sequenceName) {
        try {
            EmailData savedEmailData = emailService.sendEmail(templateId, emailData, scheduledAt, sequenceName);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("messageId", savedEmailData.getMessageId());
            responseBody.put("message", "Email sent successfully!");

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error sending email: " + e.getMessage());
        }
    }

    @DeleteMapping("/cancel-scheduled-email")
    public ResponseEntity<String> cancelScheduledEmail(@RequestParam String messageId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("API-Version", "1.2.0");

        try {
            boolean success = emailService.cancelScheduledEmail(messageId);
            return success ? ResponseEntity.ok("Scheduled email canceled successfully!") :
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .headers(responseHeaders)
                            .body("Error canceling scheduled email");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(responseHeaders)
                    .body("Failed to cancel email: " + e.getMessage());
        }
    }

    @GetMapping("/retrieve-scheduled-email")
    public ResponseEntity<?> retrieveScheduledEmail(@RequestParam String messageId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("API-Version", "1.2.0");

        try {
            GetScheduledEmailById200Response response = emailService.retrieveScheduledEmail(messageId);
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(responseHeaders)
                    .body("Error retrieving scheduled email: " + e.getMessage());
        }
    }

    @PostMapping("/webhook/email-event")
    public ResponseEntity<String> handleWebhookEvent(@RequestBody Map<String, Object> payload) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("API-Version", "1.1.6");

        try {
            emailService.handleWebhook(payload);
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body("Webhook processed successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(responseHeaders)
                    .body("Failed to process webhook: " + e.getMessage());
        }
    }

    @GetMapping("/getemail")
    public ResponseEntity<?> getEmailSummary(@RequestParam String messageId) {
        try {
            EmailSummaryDTO emailSummary = emailService.getEmailSummary(messageId);
            return ResponseEntity.ok(emailSummary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching email: " + e.getMessage());
        }
    }

}