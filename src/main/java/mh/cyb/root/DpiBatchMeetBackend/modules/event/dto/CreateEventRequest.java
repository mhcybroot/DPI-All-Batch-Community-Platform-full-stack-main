package mh.cyb.root.DpiBatchMeetBackend.modules.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateEventRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;

    private String venue;
    private String venueAddress;
    private Integer maxAttendees;
    private LocalDateTime registrationDeadline;
    private String coverImageUrl;
}
