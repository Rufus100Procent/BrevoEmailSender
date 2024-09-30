package se.stykle.brevoemailsender;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import se.stykle.brevoemailsender.service.EmailService;

import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        emailService.getAllEmails().clear();
    }


    @Test
    void setEmailService(){

    }
    private EmailMessage createEmailMessage(String email, String subject, String content, String id) {
        EmailMessage emailMessage = new EmailMessage(email, subject, content);
        emailMessage.setId(id);
        return emailMessage;
    }

    private Map<String, Object> createPayload(String email, String subject, String id, String event, String messageId, String tag, String date, String sendingIp, String senderEmail, String mirrorLink) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("subject", subject);
        payload.put("id", id);
        payload.put("message-id", messageId);
        payload.put("tag", tag);
        payload.put("date", date);
        payload.put("event", event);
        payload.put("sending_ip", sendingIp);
        payload.put("sender_email", senderEmail);
        payload.put("mirror_link", mirrorLink);
        return payload;
    }
}