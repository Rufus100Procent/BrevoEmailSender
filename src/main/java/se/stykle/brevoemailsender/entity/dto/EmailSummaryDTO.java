package se.stykle.brevoemailsender.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.stykle.brevoemailsender.Status;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailSummaryDTO {

    private UUID id;
    private String messageId;
    private String emailTo;
    private String subject;
    private Status emailStatus;
    private String mirrorLink;

}
