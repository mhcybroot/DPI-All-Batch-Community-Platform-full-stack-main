package mh.cyb.root.DpiBatchMeetBackend.modules.event.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateEventRequest {
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private String venue;
    private String venueAddress;
    private Integer maxAttendees;
    private LocalDateTime registrationDeadline;
    private String coverImageUrl;
}
