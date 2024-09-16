package se.stykle.brevoemailsender;

@SuppressWarnings("unused")
public class BrevoTemplate {
    private String templateName;
    private String subject;
    private String htmlContent;
    private String replyTo;
    private boolean isActive;
    private String attachmentUrl;
    private String toField;

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getHtmlContent() { return htmlContent; }
    public void setHtmlContent(String htmlContent) { this.htmlContent = htmlContent; }

    public String getReplyTo() { return replyTo; }
    public void setReplyTo(String replyTo) { this.replyTo = replyTo; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public String getToField() { return toField; }
    public void setToField(String toField) { this.toField = toField; }

    //</editor-fold>
}
