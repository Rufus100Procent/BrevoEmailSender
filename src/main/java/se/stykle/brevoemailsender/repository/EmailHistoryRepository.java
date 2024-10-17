package se.stykle.brevoemailsender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.stykle.brevoemailsender.entity.EmailHistory;
import se.stykle.brevoemailsender.Status;

import java.util.List;
import java.util.UUID;


@Repository
public interface EmailHistoryRepository extends JpaRepository<EmailHistory, UUID> {
    List<EmailHistory> findByMessageId(String messageId);
    List<EmailHistory> findByEmailStatus(Status status);
}