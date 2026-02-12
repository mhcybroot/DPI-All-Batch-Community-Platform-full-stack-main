package mh.cyb.root.DpiBatchMeetBackend.modules.event.dto;

import lombok.Builder;
import lombok.Data;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.RegistrationStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class RegistrationDto {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private Long userId;
    private String userName;
    private RegistrationStatus status;
    private String notes;
    private String rejectionReason;
    private Long reviewedById;
    private String reviewedByName;
    private LocalDateTime reviewedAt;
    private LocalDateTime registeredAt;
}
