package se.stykle.brevoemailsender;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailMessage emailMessage) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("API-Version", "1.0.0");
        try {
            emailService.sendEmail(emailMessage);
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body("Email sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(responseHeaders)
                    .body("Failed to send email: " + e.getMessage());
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