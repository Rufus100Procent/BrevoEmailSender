package se.stykle.brevoemailsender.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.stykle.brevoemailsender.EmailHistory;
import se.stykle.brevoemailsender.EmailMessage;
import software.xdev.brevo.api.TransactionalEmailsApi;
import software.xdev.brevo.client.ApiClient;
import software.xdev.brevo.client.ApiException;
import software.xdev.brevo.client.Configuration;
import software.xdev.brevo.client.auth.ApiKeyAuth;
import software.xdev.brevo.model.SendSmtpEmail;
import software.xdev.brevo.model.SendSmtpEmailSender;
import software.xdev.brevo.model.SendSmtpEmailToInner;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final List<EmailMessage> emails = new ArrayList<>();
    private final TransactionalEmailsApi apiInstance;
    private static final String SENDER_EMAIL = "EMAILSENDER";
    private static final String SENDER_NAME = "COMAPNYNAME.se";
    public EmailService() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey("GENERATE-API-KEYS 'https://app.brevo.com/settings/keys/api'");

        this.apiInstance = new TransactionalEmailsApi(defaultClient);
    }

    public void sendEmail(Long templateId, EmailMessage emailMessage, String scheduledAt, String batchId) {
        List<SendSmtpEmailToInner> recipients = new ArrayList<>();
        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(SENDER_EMAIL);
        sender.setName(SENDER_NAME);

        SendSmtpEmailToInner recipient = new SendSmtpEmailToInner();
        recipient.setEmail(emailMessage.getEmailTo());
        recipients.add(recipient);

        SendSmtpEmail email = new SendSmtpEmail();
        email.setSender(sender);
        email.setTo(recipients);
        email.setTemplateId(templateId);
        email.setSubject(emailMessage.getSubject());
        email.setHtmlContent(emailMessage.getContent());
        email.setParams(emailMessage.getParams());

        if (scheduledAt != null && !scheduledAt.isEmpty()) {
            try {
                OffsetDateTime scheduledDateTime = OffsetDateTime.parse(scheduledAt);
                email.setScheduledAt(scheduledDateTime);
            } catch (DateTimeParseException e) {
                logger.error("Invalid date format for scheduledAt: {}", scheduledAt);
                throw new IllegalArgumentException("Invalid date format for scheduledAt", e);
            }
        }

        if (batchId == null || batchId.isEmpty()) {
            batchId = UUID.randomUUID().toString();
        }

        if (isValidUUID(batchId)) {
            email.setBatchId(batchId);
        } else {
            logger.error("Invalid batchId format: {}", batchId);
            throw new IllegalArgumentException("Invalid batchId format. Please provide a valid UUID.");
        }

        Map<String, Object> headers = new HashMap<>();
        headers.put("X-Mailin-Tag", emailMessage.getSubject());
        email.setHeaders(headers);

        try {
            CreateSmtpEmail response = apiInstance.sendTransacEmail(email);
            String messageId = response.getMessageId();

            logger.info("Email sent successfully with messageId: {} and batchId: {}", messageId, batchId);

            // Return the messageId and batchId to the calling endpoint
            emailMessage.setMessageId(messageId);
            emailMessage.setBatchId(batchId);
        } catch (ApiException e) {
            logger.error("Error when sending email: {}", e.getResponseBody(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private boolean isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    public void handleWebhook(Map<String, Object> payload) {
        String emailTo = payload.get("email") != null ? payload.get("email").toString() : null;
        String subject = payload.get("subject") != null ? payload.get("subject").toString() : null;
        String id = payload.get("id") != null ? payload.get("id").toString() : null;

        if ((emailTo == null || emailTo.isEmpty() || subject == null || subject.isEmpty()) && (id == null || id.isEmpty())) {
            logger.warn("Webhook data is missing both email and id: {}", payload);
            throw new RuntimeException("Webhook data is not recognized: " + payload);
        }

        EmailMessage matchingEmail = null;
        if (emailTo != null && !emailTo.isEmpty() && subject != null && !subject.isEmpty()) {
            matchingEmail = emails.stream()
                    .filter(email -> email.getEmailTo().equalsIgnoreCase(emailTo) && email.getSubject().equalsIgnoreCase(subject))
                    .findFirst()
                    .orElse(null);
        }

        if (matchingEmail == null && id != null && !id.isEmpty()) {
            matchingEmail = emails.stream()
                    .filter(email -> id.equals(email.getId()))
                    .findFirst()
                    .orElse(null);
        }

        if (matchingEmail != null) {
            String mirrorLink = payload.get("mirror_link") != null ? payload.get("mirror_link").toString() : null;
            String event = payload.get("event").toString();
            String date = payload.get("date").toString();
            String sendingIp = payload.get("sending_ip") != null ? payload.get("sending_ip").toString() : null;

            // Create a new history entry
            EmailHistory historyEntry = new EmailHistory(id, emailTo, date, event, sendingIp, mirrorLink);
            matchingEmail.addHistory(historyEntry);

            // Update the current email message fields
            matchingEmail.setId(id != null ? id : matchingEmail.getId());
            matchingEmail.setMessageId(payload.get("message-id").toString());
            matchingEmail.setTag(payload.get("tag").toString());
            matchingEmail.setDate(date);
            matchingEmail.setEvent(event);
            matchingEmail.setSendingIp(sendingIp);
            matchingEmail.setSenderEmail(payload.get("sender_email").toString());
            matchingEmail.setMirrorLink(mirrorLink);

            if (List.of("open", "unique_opened", "known_open", "first_opening", "loaded_by_proxy").contains(event)) {
                matchingEmail.setOpened(true);
            }

        } else {
            logger.error("Webhook data is not recognized: {}", payload);
            throw new RuntimeException("Webhook data is not recognized: " + payload);
        }
    }


    public List<Map<String, String>> getFormattedEmails() {
        List<Map<String, String>> formattedEmails = new ArrayList<>();

        for (EmailMessage email : emails) {
            Map<String, String> emailData = new HashMap<>();
            emailData.put("Status", email.getEvent());
            emailData.put("Date", email.getDate());
            emailData.put("Subject", email.getSubject());
            emailData.put("From", email.getSenderEmail());
            emailData.put("To", email.getEmailTo());
            emailData.put("Mirror Link", email.getMirrorLink());
            formattedEmails.add(emailData);
        }

        return formattedEmails;
    }

    public List<EmailMessage> getAllEmails() {
        return emails;
    }
}

