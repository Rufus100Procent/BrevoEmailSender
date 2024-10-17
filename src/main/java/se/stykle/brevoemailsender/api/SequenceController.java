package se.stykle.brevoemailsender.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.stykle.brevoemailsender.entity.Sequence;
import se.stykle.brevoemailsender.entity.dto.EmailDataDTO;
import se.stykle.brevoemailsender.service.SequenceService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sequences")
public class SequenceController {

    private final SequenceService sequenceService;

    @Autowired
    public SequenceController(SequenceService sequenceService) {
        this.sequenceService = sequenceService;
    }

    @GetMapping
    public List<Sequence> getAllSequences() {
        return sequenceService.getAllSequences();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sequence> getSequenceById(@PathVariable UUID id) {
        Sequence sequence = sequenceService.getSequenceById(id);
        return ResponseEntity.ok(sequence);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSequence(@RequestParam String name) {
        try {
            Sequence createdSequence = sequenceService.createSequence(name);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSequence);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/{id}/emails")
    public ResponseEntity<List<EmailDataDTO>> getEmailsBySequence(@PathVariable UUID id) {
        List<EmailDataDTO> emails = sequenceService.getEmailsBySequence(id);
        return ResponseEntity.ok(emails);
    }
}
