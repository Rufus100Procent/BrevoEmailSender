package se.stykle.brevoemailsender.entity;

public enum Status {
    SCHEDULED,
    DELIVERED,
    OPENED,
    BOUNCED,
    SOFT_BOUNCED,
    HARD_BOUNCED,
    CLICKED,
    MARKED_AS_SPAM,
    CANCELLED,
    SENT,
    UNSUBSCRIBED,
    REQUESTED
}
