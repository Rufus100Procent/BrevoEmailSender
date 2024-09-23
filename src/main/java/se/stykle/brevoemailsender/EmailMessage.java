package se.stykle.brevoemailsender;

import java.util.*;
import java.util.Map;

@SuppressWarnings("unused")
public class EmailMessage {
    private String id;
    private String emailTo;
    private String messageId;
    private String tag;
    private String date;
    private String event;
    private String subject;
    private String sendingIp;
    private String senderEmail;
    private String mirrorLink;
    private String content;
    private Map<String, Object> params;
    private boolean opened;
    private final List<EmailHistory> history = new ArrayList<>();
    private String batchId;
    public EmailMessage() {
        this.params = new HashMap<>();
    }

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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getSendingIp() {
        return sendingIp;
    }

    public void setSendingIp(String sendingIp) {
        this.sendingIp = sendingIp;
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

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
//</editor-fold>
}
