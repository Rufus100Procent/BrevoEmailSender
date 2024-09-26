package se.stykle.brevoemailsender.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.stykle.brevoemailsender.BrevoTemplate;
import software.xdev.brevo.api.TransactionalEmailsApi;
import software.xdev.brevo.client.ApiClient;
import software.xdev.brevo.client.ApiException;
import software.xdev.brevo.client.Configuration;
import software.xdev.brevo.client.auth.ApiKeyAuth;
import software.xdev.brevo.model.*;
import software.xdev.brevo.model.CreateSmtpTemplate;

@Service
public class TemplateService {
    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);
    private final TransactionalEmailsApi apiInstance;

    @Value("${spring.sender.name}")
    String senderName;

    @Value("${spring.sender.email}")
    String senderEmail;

    public TemplateService() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey("GENERATE-API-KEYS 'https://app.brevo.com/settings/keys/api'");

        this.apiInstance = new TransactionalEmailsApi(defaultClient);
    }

    public GetSmtpTemplates fetchAllTemplates(Boolean isActive, int limit, int offset) {
        try {
            return apiInstance.getSmtpTemplates(isActive, (long) limit, (long) offset, null);
        } catch (ApiException e) {
            logger.error("Error fetching templates: {}", e.getResponseBody(), e);
            return null;
        }
    }

    public CreateModel createCustomTemplate(BrevoTemplate request) {
        CreateSmtpTemplate smtpTemplate = new CreateSmtpTemplate();
        CreateSmtpTemplateSender sender = new CreateSmtpTemplateSender();

        // Using static sender email and name
        sender.setEmail(senderEmail);
        sender.setName(senderName);

        smtpTemplate.setSender(sender);
        smtpTemplate.setTemplateName(request.getTemplateName());
        smtpTemplate.setSubject(request.getSubject());
        smtpTemplate.setHtmlContent(request.getHtmlContent());
        smtpTemplate.setReplyTo(request.getReplyTo());
        smtpTemplate.setIsActive(request.isActive());

        if (request.getAttachmentUrl() != null && !request.getAttachmentUrl().isEmpty()) {
            smtpTemplate.setAttachmentUrl(request.getAttachmentUrl());  // Optional attachment
        }

        if (request.getToField() != null && !request.getToField().isEmpty()) {
            smtpTemplate.setToField(request.getToField());  // Optional dynamic field for recipient email
        }

        try {
            return apiInstance.createSmtpTemplate(smtpTemplate);
        } catch (ApiException e) {
            logger.error("Error creating template: {}", e.getResponseBody(), e);
            return null;
        }
    }

    public void updateTemplate(Long templateId, BrevoTemplate request) {
        UpdateSmtpTemplate smtpTemplate = new UpdateSmtpTemplate();
        UpdateSmtpTemplateSender sender = new UpdateSmtpTemplateSender();

        sender.setEmail(senderEmail);
        sender.setName(senderName);

        smtpTemplate.setSender(sender);
        smtpTemplate.setTemplateName(request.getTemplateName());
        smtpTemplate.setSubject(request.getSubject());
        smtpTemplate.setHtmlContent(request.getHtmlContent());
        smtpTemplate.setReplyTo(request.getReplyTo());
        smtpTemplate.setIsActive(request.isActive());

        if (request.getAttachmentUrl() != null && !request.getAttachmentUrl().isEmpty()) {
            smtpTemplate.setAttachmentUrl(request.getAttachmentUrl());
        }

        if (request.getToField() != null && !request.getToField().isEmpty()) {
            smtpTemplate.setToField(request.getToField());
        }

        try {
            apiInstance.updateSmtpTemplate(templateId, smtpTemplate);
            logger.info("Template updated successfully with ID: {}", templateId);
        } catch (ApiException e) {
            logger.error("Error updating template: {}", e.getResponseBody(), e);
        }
    }

    public boolean deleteTemplate(Long templateId) {
        try {
            apiInstance.deleteSmtpTemplate(templateId);
            return true;
        } catch (ApiException e) {
            logger.error("Error deleting template: {}", e.getResponseBody(), e);
            return false;
        }
    }

    public boolean deactivateTemplate(Long templateId) {
        try {
            UpdateSmtpTemplate smtpTemplate = new UpdateSmtpTemplate();
            smtpTemplate.setIsActive(false);

            apiInstance.updateSmtpTemplate(templateId, smtpTemplate);
            logger.info("Template deactivated successfully with ID: {}", templateId);
            return true;
        } catch (ApiException e) {
            logger.error("Error deactivating template: {}", e.getResponseBody(), e);
            return false;
        }
    }

    public boolean activateTemplate(Long templateId) {
        try {
            UpdateSmtpTemplate smtpTemplate = new UpdateSmtpTemplate();
            smtpTemplate.setIsActive(true);

            apiInstance.updateSmtpTemplate(templateId, smtpTemplate);
            logger.info("Template activated successfully with ID: {}", templateId);
            return true;
        } catch (ApiException e) {
            logger.error("Error activating template: {}", e.getResponseBody(), e);
            return false;
        }
    }

}
