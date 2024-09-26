package se.stykle.brevoemailsender.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.stykle.brevoemailsender.EmailMessage;
import se.stykle.brevoemailsender.service.EmailService;
import software.xdev.brevo.model.GetScheduledEmailById200Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-email")
    public ResponseEntity<Map<String, String>> sendEmail(
            @RequestParam(required = false) Long templateId,
            @RequestBody EmailMessage emailMessage,
            @RequestParam(required = false) String scheduledAt) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("API-Version", "1.2.0");

        try {
            emailService.sendEmail(templateId, emailMessage, scheduledAt);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Email sent successfully!");
            responseBody.put("messageId", emailMessage.getMessageId());

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(responseBody);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(responseHeaders)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(responseHeaders)
                    .body(Map.of("error", "Failed to send email: " + e.getMessage()));
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


    // receives webhooks from brevo
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

    @GetMapping("/emails")
    public ResponseEntity<List<Map<String, String>>> getFormattedEmails() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("API-Version", "1.0.0");

        List<Map<String, String>> emails = emailService.getFormattedEmails();
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(emails);
    }

    @GetMapping("/emails/raw-data")
    public ResponseEntity<List<EmailMessage>> getAllEmails() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("API-Version", "1.0.0");

        List<EmailMessage> emails = emailService.getAllEmails();
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(emails);
    }
}