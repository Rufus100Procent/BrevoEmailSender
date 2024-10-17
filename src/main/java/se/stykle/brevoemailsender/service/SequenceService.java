package se.stykle.brevoemailsender.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.stykle.brevoemailsender.entity.EmailData;
import se.stykle.brevoemailsender.entity.EmailHistory;
import se.stykle.brevoemailsender.entity.Sequence;
import se.stykle.brevoemailsender.entity.dto.EmailDataDTO;
import se.stykle.brevoemailsender.entity.dto.EmailHistoryDTO;
import se.stykle.brevoemailsender.repository.EmailDataRepository;
import se.stykle.brevoemailsender.repository.SequenceRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SequenceService {

    private final SequenceRepository sequenceRepository;
    private final EmailDataRepository emailDataRepository;
    @Autowired
    public SequenceService(SequenceRepository sequenceRepository, EmailDataRepository emailDataRepository) {
        this.sequenceRepository = sequenceRepository;
        this.emailDataRepository = emailDataRepository;
    }


    public List<Sequence> getAllSequences() {
        return sequenceRepository.findAll();
    }

    public Sequence getSequenceById(UUID id) {
        return sequenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sequence not found with id: " + id));
    }

    public Sequence createSequence(String name) {
        if (sequenceRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Sequence name already exists: " + name);
        }

        Sequence newSequence = new Sequence();
        newSequence.setName(name);
        newSequence.setTotalEmailsSent(0);
        newSequence.setScheduledCount(0);
        newSequence.setDeliveredCount(0);
        newSequence.setOpenedCount(0);
        newSequence.setRequestedCount(0);

        return sequenceRepository.save(newSequence);
    }

    public List<EmailDataDTO> getEmailsBySequence(UUID sequenceId) {
        Sequence sequence = sequenceRepository.findById(sequenceId)
                .orElseThrow(() -> new EntityNotFoundException("Sequence not found with id: " + sequenceId));

        List<EmailData> emails = emailDataRepository.findBySequence(sequence);

        return emails.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private EmailDataDTO mapToDTO(EmailData emailData) {
        List<EmailHistoryDTO> historyDTOs = emailData.getHistory().stream()
                .map(this::mapToHistoryDTO)
                .collect(Collectors.toList());

        return new EmailDataDTO(
                emailData.getId(),
                emailData.getMessageId(),
                emailData.getEmailTo(),
                emailData.getDate(),
                emailData.getEmailStatus(),
                emailData.getSubject(),
                emailData.getMirrorLink(),
                emailData.getSequence().getName(),
                historyDTOs
        );
    }

    private EmailHistoryDTO mapToHistoryDTO(EmailHistory history) {
        return new EmailHistoryDTO(
                history.getId(),
                history.getMessageId(),
                history.getEmail(),
                history.getDate(),
                history.getEmailStatus(),
                history.getMirrorLink()
        );
    }
}
