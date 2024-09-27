package se.stykle.brevoemailsender;

import java.util.*;
import java.util.Map;

@SuppressWarnings("unused")
public class EmailMessage {
    private String id;
    private String emailTo;
    private String messageId;
    private String date;
    private String emailStatus; // event
    private String subject;
    private String senderEmail;
    private String mirrorLink;
    private String content;
    private Map<String, Object> params;
    private boolean opened;
    private String scheduledDate;
    private final List<EmailHistory> history = new ArrayList<>();

    public EmailMessage(String recipient, String subject, String content) {
        this.emailTo = recipient;
        this.subject = subject;
        this.content = content;
        this.params = new HashMap<>();
        this.opened = false;
    }

    public EmailMessage(String recipient, String subject, String content, Map<String, Object> params) {
        this.emailTo = recipient;
        this.subject = subject;
        this.content = content;
        this.params = params != null ? params : new HashMap<>();
        this.opened = false;
    }

    public String isScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    // Add an entry to the history list
    public void addHistory(EmailHistory historyEntry) {
        this.history.add(historyEntry);
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public List<EmailHistory> getHistory() {
        return history;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String recipient) {
        this.emailTo = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params != null ? params : new HashMap<>();
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(String emailStatus) {
        this.emailStatus = emailStatus;
    }


    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getMirrorLink() {
        return mirrorLink;
    }

    public void setMirrorLink(String mirrorLink) {
        this.mirrorLink = mirrorLink;
    }
    public EmailMessage() {
        this.params = new HashMap<>();
    }

    public String getScheduledDate() {
        return scheduledDate;
    }
//</editor-fold>
}
