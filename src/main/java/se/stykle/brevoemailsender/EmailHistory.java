package se.stykle.brevoemailsender;

@SuppressWarnings("unused")
public class EmailHistory {
    private String id;
    private String email;
    private String date;
    private String emailStatus;
    private String mirrorLink;

    public EmailHistory(String id, String email, String date, String event, String mirrorLink) {
        this.id = id;
        this.email = email;
        this.date = date;
        this.emailStatus = event;
        this.mirrorLink = mirrorLink;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getMirrorLink() {
        return mirrorLink;
    }

    public void setMirrorLink(String mirrorLink) {
        this.mirrorLink = mirrorLink;
    }

//</editor-fold>

}
