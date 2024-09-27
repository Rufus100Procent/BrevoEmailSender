//package se.stykle.brevoemailsender;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.MockitoAnnotations;
//import se.stykle.brevoemailsender.service.EmailService;
//
//import java.util.HashMap;
//import java.util.Map;
//import static org.junit.jupiter.api.Assertions.*;
//
//class EmailServiceTest {
//
//    @InjectMocks
//    private EmailService emailService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @AfterEach
//    void tearDown() {
//        emailService.getAllEmails().clear();
//    }
//
//    @Test
//    void testHandleWebhookWithValidEmailAndSubject() {
//        // Arrange
//        EmailMessage emailMessage = createEmailMessage("test@example.com", "Test Subject", "Test Content", "123");
//        emailService.getAllEmails().add(emailMessage);
//
//        Map<String, Object> payload = createPayload(
//                "test@example.com",
//                "Test Subject",
//                "123",
//                "delivered",
//                "message-id-123",
//                "Test Tag",
//                "2024-08-24",
//                "127.0.0.1",
//                "sender@example.com",
//                "https://example.com/mirror"
//        );
//
//        // Act
//        emailService.handleWebhook(payload);
//
//        // Assert
//        EmailMessage updatedEmail = emailService.getAllEmails().get(0);
//        assertEquals("delivered", updatedEmail.getEmailStatus());
//        assertEquals("123", updatedEmail.getId());
//        assertFalse(updatedEmail.getHistory().isEmpty());
//    }
//
//    @Test
//    void testHandleWebhookWithMissingEmailAndSubjectButValidId() {
//        // Arrange
//        EmailMessage emailMessage = createEmailMessage("test2@example.com", "Test Subject 2", "Test Content 2", "456");
//        emailService.getAllEmails().add(emailMessage);
//
//        Map<String, Object> payload = createPayload(
//                "",
//                "",
//                "456",
//                "opened",
//                "message-id-456",
//                "Test Tag 2",
//                "2024-08-25",
//                "192.168.0.1",
//                "sender2@example.com",
//                "https://example.com/mirror2"
//        );
//
//        // Act
//        emailService.handleWebhook(payload);
//
//        // Assert
//        EmailMessage updatedEmail = emailService.getAllEmails().get(0);
//        assertEquals("opened", updatedEmail.getEmailStatus());
//        assertEquals("456", updatedEmail.getId());
//        assertFalse(updatedEmail.getHistory().isEmpty());
//    }
//
//    @Test
//    void testHandleWebhookWithMissingEmailAndId() {
//        // Arrange
//        Map<String, Object> payload = createPayload("", "", "", "", "", "", "", "", "", "");
//
//        // Act & Assert
//        RuntimeException thrown = assertThrows(RuntimeException.class, () -> emailService.handleWebhook(payload));
//
//        assertTrue(thrown.getMessage().contains("Webhook data is not recognized"));
//    }
//
//    @Test
//    void testHandleWebhookWithNoMatchingEmail() {
//        // Arrange
//        Map<String, Object> payload = createPayload(
//                "nonexistent@example.com",
//                "Nonexistent Subject",
//                "789",
//                "delivered",
//                "message-id-789",
//                "Test Tag",
//                "2024-08-24",
//                "127.0.0.1",
//                "sender@example.com",
//                "https://example.com/mirror"
//        );
//
//        // Act & Assert
//        RuntimeException thrown = assertThrows(RuntimeException.class, () -> emailService.handleWebhook(payload));
//
//        assertTrue(thrown.getMessage().contains("Webhook data is not recognized"));
//    }
//
//    // Private helper methods
//    private EmailMessage createEmailMessage(String email, String subject, String content, String id) {
//        EmailMessage emailMessage = new EmailMessage(email, subject, content);
//        emailMessage.setId(id);
//        return emailMessage;
//    }
//
//    private Map<String, Object> createPayload(String email, String subject, String id, String event, String messageId, String tag, String date, String sendingIp, String senderEmail, String mirrorLink) {
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("email", email);
//        payload.put("subject", subject);
//        payload.put("id", id);
//        payload.put("message-id", messageId);
//        payload.put("tag", tag);
//        payload.put("date", date);
//        payload.put("event", event);
//        payload.put("sending_ip", sendingIp);
//        payload.put("sender_email", senderEmail);
//        payload.put("mirror_link", mirrorLink);
//        return payload;
//    }
//}