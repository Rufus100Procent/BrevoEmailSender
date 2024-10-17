package se.stykle.brevoemailsender.service;


import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.stykle.brevoemailsender.entity.EmailData;
import se.stykle.brevoemailsender.entity.EmailHistory;
import se.stykle.brevoemailsender.entity.Sequence;
import se.stykle.brevoemailsender.entity.Status;
import se.stykle.brevoemailsender.entity.dto.EmailSummaryDTO;
import se.stykle.brevoemailsender.repository.EmailDataRepository;
import se.stykle.brevoemailsender.repository.EmailHistoryRepository;
import se.stykle.brevoemailsender.repository.SequenceRepository;
import software.xdev.brevo.api.TransactionalEmailsApi;
import software.xdev.brevo.client.ApiClient;
import software.xdev.brevo.client.ApiException;
import software.xdev.brevo.client.Configuration;
import software.xdev.brevo.client.auth.ApiKeyAuth;
import software.xdev.brevo.model.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
@Service
public class EmailService {

    private TransactionalEmailsApi apiInstance;
    private final EmailDataRepository emailDataRepository;
    private final EmailHistoryRepository emailHistoryRepository;
    private final SequenceRepository sequenceRepository;

    @Value("${spring.sender.name}")
    private String senderName;

    @Value("${spring.sender.email}")
    private String senderEmail;

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    public EmailService(EmailDataRepository emailDataRepository,
                        EmailHistoryRepository emailHistoryRepository,
                        SequenceRepository sequenceRepository) {
        this.emailDataRepository = emailDataRepository;
        this.emailHistoryRepository = emailHistoryRepository;
        this.sequenceRepository = sequenceRepository;

        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        System.out.println(apiKeyAuth);
        apiKeyAuth.setApiKey(brevoApiKey);
        this.apiInstance = new TransactionalEmailsApi(defaultClient);
    }
    @PostConstruct
    private void init() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.addDefaultHeader("api-key", brevoApiKey);  // Manually adding API key globally
        this.apiInstance = new TransactionalEmailsApi(defaultClient);
    }
    /**
     * Sends an email using the specified template and schedules it if required.
     *
     * @param templateId    The ID of the email template. If null, a plain HTML email is sent.
     * @param emailData     The EmailData object containing email details.
     * @param scheduledAt   The scheduled time for sending the email in ISO-8601 format.
     * @param sequenceName  The name of the sequence to associate the email with. If null or empty, defaults to "default_Sequence".
     * @return The saved EmailData object upon successful sending.
     */
    @Transactional
    public EmailData sendEmail(Long templateId, EmailData emailData, String scheduledAt, String sequenceName) {
        try {
            validateEmailData(emailData);
            assignSequence(emailData, sequenceName);
            emailData.setSenderEmail(senderEmail);

            SendSmtpEmail email = prepareSendEmail(emailData, templateId, scheduledAt);
            CreateSmtpEmail response = executeSendEmail(email);

            processSendEmailResponse(response, emailData, scheduledAt != null && !scheduledAt.isEmpty());

            log.info("Email sent successfully with messageId: {}", emailData.getMessageId());
            return emailData;
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private void validateEmailData(EmailData emailData) {
        if (emailData.getEmailTo() == null || emailData.getEmailTo().isEmpty()) {
            throw new IllegalArgumentException("Recipient email cannot be empty");
        }
        if (emailData.getParams() == null || emailData.getParams().isEmpty()) {
            throw new IllegalArgumentException("Params cannot be empty");
        }
    }

    /**
     * Assigns a sequence to the EmailData. Creates a default sequence if none is provided or found.
     *
     * @param emailData    The EmailData object to assign a sequence to.
     * @param sequenceName The name of the sequence. Defaults to "default_Sequence" if null or empty.
     */
    private void assignSequence(EmailData emailData, String sequenceName) {
        String seqName = (sequenceName == null || sequenceName.isEmpty()) ? "default_Sequence" : sequenceName;
        Sequence sequence = sequenceRepository.findByName(seqName)
                .orElseGet(() -> {
                    Sequence newSequence = new Sequence();
                    newSequence.setName(seqName);
                    newSequence.setTotalEmailsSent(0);
                    newSequence.setRequestedCount(0);
                    newSequence.setScheduledCount(0);
                    newSequence.setDeliveredCount(0);
                    newSequence.setOpenedCount(0);
                    return sequenceRepository.save(newSequence);
                });
        emailData.setSequence(sequence);
    }

    private SendSmtpEmail prepareSendEmail(EmailData emailData, Long templateId, String scheduledAt) {
        SendSmtpEmail email = new SendSmtpEmail();
        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(senderEmail);
        sender.setName(senderName);
        email.setSender(sender);

        SendSmtpEmailToInner recipient = new SendSmtpEmailToInner();
        recipient.setEmail(emailData.getEmailTo());
        email.setTo(Collections.singletonList(recipient));

        if (templateId != null) {
            email.setTemplateId(templateId);
            log.info("Using template ID: {}", templateId);
        } else {
            email.setHtmlContent(emailData.getContent());
            if (emailData.getSubject() != null && !emailData.getSubject().isEmpty()) {
                email.setSubject(emailData.getSubject());
            } else {
                throw new IllegalArgumentException("Subject cannot be empty when templateId is not specified");
            }
        }

        email.setParams(emailData.getParams());

        if (scheduledAt != null && !scheduledAt.isEmpty()) {
            try {
                // Add more logging for debugging
                log.info("Parsing scheduledAt: {}", scheduledAt);

                OffsetDateTime scheduledDateTime = OffsetDateTime.parse(scheduledAt);
                email.setScheduledAt(scheduledDateTime);

                // Also update the emailData object
                emailData.setScheduledDate(scheduledAt);
            } catch (DateTimeParseException e) {
                log.error("Invalid date format for scheduledAt: {}", scheduledAt, e);
                throw new IllegalArgumentException("Invalid date format for scheduledAt", e);
            }
        } else {
            emailData.setScheduledDate("No scheduled date");
        }

        Map<String, Object> headers = new HashMap<>();
        headers.put("X-Mailin-Tag", emailData.getSubject() != null ? emailData.getSubject() : "No Subject");
        email.setHeaders(headers);
        log.debug("Sending email with API key: {}", brevoApiKey);
        return email;
    }

    /**
     * Executes the sending of the email using the Brevo API.
     *
     * @param email The SendSmtpEmail object to send.
     * @return The CreateSmtpEmail response.
     */
    private CreateSmtpEmail executeSendEmail(SendSmtpEmail email) throws ApiException {
        return apiInstance.sendTransacEmail(email);
    }

    /**
     * Processes the response from the Brevo API after sending the email.
     *
     * @param response        The CreateSmtpEmail response from Brevo.
     * @param emailData       The original EmailData object.
     * @param isScheduledSend Flag indicating if the email is scheduled.
     */
    @Transactional
    protected void processSendEmailResponse(CreateSmtpEmail response, EmailData emailData, boolean isScheduledSend) {
        String messageId = response.getMessageId();

        emailData.setMessageId(messageId);
        emailData.setEmailStatus(Status.REQUESTED);
        emailData.setDate(OffsetDateTime.now());

        EmailData savedEmailData = emailDataRepository.save(emailData);

        // Create and save EmailHistory
        EmailHistory historyEntry = new EmailHistory();
        historyEntry.setMessageId(savedEmailData.getMessageId());
        historyEntry.setEmail(savedEmailData.getEmailTo());
        historyEntry.setEmailStatus(Status.REQUESTED);
        historyEntry.setDate(OffsetDateTime.now());
        historyEntry.setMirrorLink(null); // updated via webhook later
        historyEntry.setEmailData(savedEmailData);
        emailHistoryRepository.save(historyEntry);

        // Update sequence counts
        Sequence sequence = savedEmailData.getSequence();
        sequence.setTotalEmailsSent(sequence.getTotalEmailsSent() + 1);
        sequence.setRequestedCount(sequence.getRequestedCount() + 1);
        if (isScheduledSend) {
            sequence.setScheduledCount(sequence.getScheduledCount() + 1);
        }
        sequenceRepository.save(sequence);

    }

    /**
     * Handles incoming webhook payloads from Brevo.
     *
     * @param payload The webhook payload as a Map.
     */
    @Transactional
    public void handleWebhook(Map<String, Object> payload) {
        log.info("Webhook data: {}", payload);
        // Extract and validate essential fields
        final String emailTo = payload.get("email") != null ? payload.get("email").toString() : null;
        final String messageId = payload.get("message-id") != null ? payload.get("message-id").toString() : null;

        if (emailTo == null || messageId == null) {
            log.warn("Webhook data is missing email or message-id: {}", payload);
            throw new RuntimeException("Webhook data is not recognized: " + payload);
        }

        // Retrieve EmailData by messageId
        EmailData emailData = emailDataRepository.findByMessageId(messageId)
                .filter(ed -> ed.getEmailTo().equalsIgnoreCase(emailTo))
                .orElseThrow(() -> {
                    log.error("No matching email found for messageId: {}", messageId);
                    return new RuntimeException("No matching email found for messageId: " + messageId);
                });

        // Extract event details
        final String eventStr = payload.get("event") != null ? payload.get("event").toString().toUpperCase() : null;
        final String dateStr = payload.get("date") != null ? payload.get("date").toString() : null;
        final String mirrorLink = payload.get("mirror_link") != null ? payload.get("mirror_link").toString() : null;

        if (eventStr == null) {
            log.warn("Webhook event is missing: {}", payload);
            throw new RuntimeException("Webhook event is missing: " + payload);
        }

        // Parse event status
        final Status eventStatus;
        try {
            eventStatus = Status.valueOf(eventStr);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown event status: {}", eventStr);
            throw new RuntimeException("Unknown event status: " + eventStr);
        }

        // Parse event date
        final OffsetDateTime eventDate;
        try {
            eventDate = dateStr != null ? OffsetDateTime.parse(dateStr) : OffsetDateTime.now();
        } catch (DateTimeParseException e) {
            log.warn("Invalid date format in webhook: {}", dateStr);
            throw new RuntimeException("Invalid date format in webhook: " + dateStr);
        }

        emailData.setDate(eventDate);
        emailData.setEmailStatus(eventStatus);
        emailData.setMirrorLink(mirrorLink);

        emailDataRepository.save(emailData);

        EmailHistory historyEntry = new EmailHistory();
        historyEntry.setMessageId(emailData.getMessageId());
        historyEntry.setEmail(emailData.getEmailTo());
        historyEntry.setEmailStatus(eventStatus);
        historyEntry.setDate(eventDate);
        historyEntry.setMirrorLink(mirrorLink);
        historyEntry.setEmailData(emailData);
        emailHistoryRepository.save(historyEntry);

        // Adjust Sequence counts based on event
        Sequence sequence = emailData.getSequence();
        switch (eventStatus) {
            case DELIVERED:
            case OPENED:
            case BOUNCED:
            case SOFT_BOUNCED:
            case HARD_BOUNCED:
            case CLICKED:
            case MARKED_AS_SPAM:
            case UNSUBSCRIBED:
            case CANCELLED:
                // For these events, decrement requestedCount
                sequence.setRequestedCount(Math.max(sequence.getRequestedCount() - 1, 0));
                break;
            case SENT:
                // If event is SENT, decrement scheduledCount
                sequence.setScheduledCount(Math.max(sequence.getScheduledCount() - 1, 0));
                break;
            // Add other cases as necessary
            default:
                log.warn("Unhandled event status: {}", eventStatus);
                break;
        }

        sequenceRepository.save(sequence);
    }

    public EmailSummaryDTO getEmailSummary(String messageId) {
        return emailDataRepository.findEmailSummaryByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("Email not found with messageId: " + messageId));
    }


    public GetScheduledEmailById200Response retrieveScheduledEmail(String messageId) {
        try {
            return apiInstance.getScheduledEmailById(messageId, null, null, null, null, null, null);
        } catch (ApiException e) {
            log.error("Error retrieving scheduled email: {}", e.getResponseBody(), e);
            throw new RuntimeException("Failed to retrieve scheduled email: " + e.getMessage());
        }
    }

    public boolean cancelScheduledEmail(String messageId) {
        try {
            apiInstance.deleteScheduledEmailById(messageId);
            return true;
        } catch (ApiException e) {
            log.error("Error canceling scheduled email: {}", e.getResponseBody(), e);
            throw new RuntimeException("Failed to cancel scheduled email: " + e.getMessage());
        }
    }
}

