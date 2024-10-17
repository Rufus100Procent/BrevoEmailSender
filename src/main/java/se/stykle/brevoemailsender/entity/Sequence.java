package se.stykle.brevoemailsender.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sequences")
public class Sequence {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "total_emails_sent", nullable = false)
    private int totalEmailsSent;

    @Column(name = "scheduled_count", nullable = false)
    private int scheduledCount;

    @Column(name = "delivered_count", nullable = false)
    private int deliveredCount;

    @Column(name = "opened_count", nullable = false)
    private int openedCount;

    @Column(name = "requested_count", nullable = false)
    private int requestedCount;
}