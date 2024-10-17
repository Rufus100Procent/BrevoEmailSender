package se.stykle.brevoemailsender.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.stykle.brevoemailsender.MapToJsonConverter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "email_data")
public class EmailData {

    @Id
    @GeneratedValue
    private UUID id; // Auto-generated UUID

    @Column(name = "email_to", nullable = false)
    private String emailTo;

    @Column(name = "message_id", unique = true, nullable = false)
    private String messageId;

    @Column(name = "date")
    private OffsetDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name = "email_status", nullable = false)
    private Status emailStatus;

    @Column(name = "subject")
    private String subject;

    @Column(name = "sender_email", nullable = false)
    private String senderEmail;

    @Column(name = "mirror_link")
    private String mirrorLink;

    @Column(name = "content")
    private String content;

    // Use the MapToJsonConverter to store the params field as a JSON string
    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "params", columnDefinition = "TEXT")
    private Map<String, Object> params;

    @Column(name = "opened")
    private boolean opened;

    @Column(name = "scheduled_date")
    private String scheduledDate;

    @ManyToOne
    @JoinColumn(name = "sequence_id")
    private Sequence sequence;

    @OneToMany(mappedBy = "emailData", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmailHistory> history;
}
