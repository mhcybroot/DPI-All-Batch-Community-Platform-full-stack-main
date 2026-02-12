package mh.cyb.root.DpiBatchMeetBackend.modules.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreatePollRequest {
    private Long eventId; // Optional if standalone

    @NotBlank(message = "Question is required")
    private String question;

    @NotEmpty(message = "At least two options are required")
    private List<String> options;

    private boolean multipleChoice;
    private boolean anonymous;
    private LocalDateTime deadline;
}
