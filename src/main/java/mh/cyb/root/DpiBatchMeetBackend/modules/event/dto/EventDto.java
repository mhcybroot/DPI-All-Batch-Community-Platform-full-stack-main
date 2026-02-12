package mh.cyb.root.DpiBatchMeetBackend.modules.event.dto;

import lombok.Data;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.EventStatus;

import java.time.LocalDateTime;

@Data
public class EventDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private String venue;
    private String venueAddress;
    private Long organizerId;
    private String organizerName;
    private EventStatus status;
    private Integer maxAttendees;
    private Integer currentAttendees;
    private LocalDateTime registrationDeadline;
    private String coverImageUrl;
    private LocalDateTime createdAt;
}
