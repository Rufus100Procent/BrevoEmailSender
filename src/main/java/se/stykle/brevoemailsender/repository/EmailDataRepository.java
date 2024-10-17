package se.stykle.brevoemailsender.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.stykle.brevoemailsender.entity.EmailData;
import se.stykle.brevoemailsender.entity.Sequence;
import se.stykle.brevoemailsender.entity.dto.EmailSummaryDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailDataRepository extends JpaRepository<EmailData, UUID> {
    Optional<EmailData> findByMessageId(String messageId);

    @Query("SELECT new se.stykle.brevoemailsender.entity.dto.EmailSummaryDTO(e.id, e.messageId, e.emailTo, e.subject, e.emailStatus, e.mirrorLink) " +
            "FROM EmailData e WHERE e.messageId = :messageId")
    Optional<EmailSummaryDTO> findEmailSummaryByMessageId(@Param("messageId") String messageId);

    List<EmailData> findBySequence(Sequence sequence);

}