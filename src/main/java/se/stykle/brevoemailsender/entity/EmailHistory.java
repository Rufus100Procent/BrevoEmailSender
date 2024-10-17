package se.stykle.brevoemailsender.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.stykle.brevoemailsender.Status;

import java.time.OffsetDateTime;
import java.util.UUID;




@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "email_history")
public class EmailHistory {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "message_id", nullable = false)
    private String messageId;

    @Column(name = "email")
    private String email;

    @Column(name = "date")
    private OffsetDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name = "email_status", nullable = false)
    private Status emailStatus;

    @Column(name = "mirror_link")
    private String mirrorLink;

    @ManyToOne
    @JoinColumn(name = "email_data_id")
    private EmailData emailData;
}