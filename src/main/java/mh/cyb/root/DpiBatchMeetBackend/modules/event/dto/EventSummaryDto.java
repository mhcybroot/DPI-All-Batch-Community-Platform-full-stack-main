package mh.cyb.root.DpiBatchMeetBackend.modules.event.dto;

import lombok.Data;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.EventStatus;

import java.time.LocalDateTime;

@Data
public class EventSummaryDto {
    private Long id;
    private String title;
    private LocalDateTime eventDate;
    private String venue;
    private EventStatus status;
    private String coverImageUrl;
    private Integer maxAttendees;
    private Integer currentAttendees;
}
