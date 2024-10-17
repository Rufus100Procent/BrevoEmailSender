package se.stykle.brevoemailsender.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.stykle.brevoemailsender.entity.Status;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailHistoryDTO {

    private UUID id;
    private String messageId;
    private String email;
    private OffsetDateTime date;
    private Status emailStatus;
    private String mirrorLink;

}
