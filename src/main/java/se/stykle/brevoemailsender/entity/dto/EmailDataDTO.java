package se.stykle.brevoemailsender.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.stykle.brevoemailsender.Status;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailDataDTO {

    private UUID id;
    private String messageId;
    private String emailTo;
    private OffsetDateTime date;
    private Status emailStatus;
    private String subject;
    private String mirrorLink;
    private String sequenceName;
    private List<EmailHistoryDTO> history;

}
