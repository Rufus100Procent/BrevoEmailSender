package se.stykle.brevoemailsender.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.stykle.brevoemailsender.EmailHistory;
import se.stykle.brevoemailsender.EmailMessage;
import software.xdev.brevo.api.TransactionalEmailsApi;
import software.xdev.brevo.client.ApiClient;
import software.xdev.brevo.client.ApiException;
import software.xdev.brevo.client.Configuration;
import software.xdev.brevo.client.auth.ApiKeyAuth;
import software.xdev.brevo.model.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final List<EmailMessage> emails = new ArrayList<>();
    private final TransactionalEmailsApi apiInstance;

    @Value("${spring.sender.name}")
    String senderName;

    @Value("${spring.sender.email}")
    String senderEmail;

    public EmailService() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey("GENERATE-API-KEYS 'https://app.brevo.com/settings/keys/api'");

        this.apiInstance = new TransactionalEmailsApi(defaultClient);
    }

     //2024-09-26T10:56:00+02:00
    public void sendEmail(Long templateId, EmailMessage emailMessage, String scheduledAt) {
        validateParamAndEmailTo(emailMessage);
     public void sendEmail(Long templateId, EmailMessage emailMessage, String scheduledAt) {
         validateParamAndEmailTo(emailMessage);

         if (templateId == null) {
             handleEmptyTemplate(emailMessage);
         }

         SendSmtpEmail email = new SendSmtpEmail();
         SendSmtpEmailSender sender = new SendSmtpEmailSender();
         sender.setEmail(senderEmail);
         sender.setName(senderName);
         email.setSender(sender);

         List<SendSmtpEmailToInner> recipients = new ArrayList<>();
         SendSmtpEmailToInner recipient = new SendSmtpEmailToInner();
         recipient.setEmail(emailMessage.getEmailTo());
         recipients.add(recipient);
         email.setTo(recipients);

         if (templateId != null) {
             email.setTemplateId(templateId);
             logger.info("Using template ID: {}", templateId);
         } else {
             email.setHtmlContent(emailMessage.getContent());
             email.setSubject(emailMessage.getSubject());
         }

         email.setParams(emailMessage.getParams());

         if (scheduledAt != null && !scheduledAt.isEmpty()) {
             try {
                 OffsetDateTime scheduledDateTime = OffsetDateTime.parse(scheduledAt);
                 email.setScheduledAt(scheduledDateTime);
                 emailMessage.setScheduledDate(scheduledAt); // Store the scheduled date
             } catch (DateTimeParseException e) {
                 logger.error("Invalid date format for scheduledAt: {}", scheduledAt);
                 throw new IllegalArgumentException("Invalid date format for scheduledAt", e);
             }
         } else {
             emailMessage.setScheduledDate("No scheduled date"); // Default if no scheduledAt is provided
         }

         Map<String, Object> headers = new HashMap<>();
         headers.put("X-Mailin-Tag", emailMessage.getSubject());
         email.setHeaders(headers);

         try {
             CreateSmtpEmail response = apiInstance.sendTransacEmail(email);
             String messageId = response.getMessageId();
             emailMessage.setMessageId(messageId);
             emailMessage.setSenderEmail(senderEmail);
             emailMessage.setEmailStatus("requested");

             // Save to history
             EmailHistory historyEntry = new EmailHistory(
                     messageId,
                     emailMessage.getEmailTo(),
                     emailMessage.getDate(),
                     emailMessage.getEmailStatus(),
                     null // Mirror link will be added later from the webhook
             );
             emailMessage.addHistory(historyEntry);

             // Add email message to the list (or other storage mechanism)
             emails.add(emailMessage);

             logger.info("Email sent successfully with messageId: {}", messageId);
         } catch (ApiException e) {
             logger.error("Error when sending email: {}", e.getResponseBody(), e);
             throw new RuntimeException("Failed to send email: " + e.getMessage());
         }
     }

    private void handleEmptyTemplate(EmailMessage emailMessage) {

        if (emailMessage.getContent() == null || emailMessage.getContent().isEmpty()) {
            throw new IllegalArgumentException("Email Body cannot be empty when templateId is not specified");
        }
        if (emailMessage.getSubject() == null || emailMessage.getSubject().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be empty when templateId is not specified");
        }

    }

    private void validateParamAndEmailTo(EmailMessage emailMessage) {
        if (emailMessage.getEmailTo() == null || emailMessage.getEmailTo().isEmpty()) {
            throw new IllegalArgumentException("Recipient email cannot be empty");
        }
        if (emailMessage.getSubject() == null || emailMessage.getSubject().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be empty");
        }
    }

    public GetScheduledEmailById200Response retrieveScheduledEmail(String messageId) {
        try {
            return apiInstance.getScheduledEmailById(messageId, null, null, null, null, null, null);
        } catch (ApiException e) {
            logger.error("Error retrieving scheduled email: {}", e.getResponseBody(), e);
            throw new RuntimeException("Failed to retrieve scheduled email: " + e.getMessage());
        }
    }

    public boolean cancelScheduledEmail(String messageId) {
        try {
            apiInstance.deleteScheduledEmailById(messageId);
            return true;
        } catch (ApiException e) {
            logger.error("Error canceling scheduled email: {}", e.getResponseBody(), e);
            throw new RuntimeException("Failed to cancel scheduled email: " + e.getMessage());
        }
    }


    public void handleWebhook(Map<String, Object> payload) {
        String emailTo = payload.get("email") != null ? payload.get("email").toString() : null;
        String messageId = payload.get("message-id") != null ? payload.get("message-id").toString() : null;

        if (emailTo == null || messageId == null) {
            logger.warn("Webhook data is missing email or message-id: {}", payload);
            throw new RuntimeException("Webhook data is not recognized: " + payload);
        }

        EmailMessage matchingEmail = emails.stream()
                .filter(email -> email.getEmailTo().equalsIgnoreCase(emailTo) && email.getMessageId().equals(messageId))
                .findFirst()
                .orElse(null);

        if (matchingEmail != null) {
            String id = payload.get("id") != null ? payload.get("id").toString() : null;
            String event = payload.get("event") != null ? payload.get("event").toString() : null;
            String date = payload.get("date") != null ? payload.get("date").toString() : null;
            String mirrorLink = payload.get("mirror_link") != null ? payload.get("mirror_link").toString() : null;

            // Update the current email message fields
            matchingEmail.setId(id != null ? id : matchingEmail.getId());
            matchingEmail.setDate(date);
            matchingEmail.setEmailStatus(event);
            matchingEmail.setMirrorLink(mirrorLink);

            // Create a new history entry
            EmailHistory historyEntry = new EmailHistory(
                    id, emailTo, date, event, mirrorLink
            );
            matchingEmail.addHistory(historyEntry);

            logger.info("Email updated successfully for messageId: {}", messageId);
        } else {
            logger.error("No matching email found for messageId: {}", messageId);
            throw new RuntimeException("No matching email found for messageId: " + messageId);
        }
    }
    public List<Map<String, String>> getFormattedEmails() {
        List<Map<String, String>> formattedEmails = new ArrayList<>();

        for (EmailMessage email : emails) {
            Map<String, String> emailData = new HashMap<>();
            emailData.put("Status", email.getEmailStatus());
            emailData.put("Date", email.getDate());
            emailData.put("Scheduled Date", email.getScheduledDate());
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

