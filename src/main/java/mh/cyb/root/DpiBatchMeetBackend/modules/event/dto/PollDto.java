package mh.cyb.root.DpiBatchMeetBackend.modules.event.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PollDto {
    private Long id;
    private Long eventId;
    private String question;
    private Long createdBy;
    private boolean multipleChoice;
    private boolean anonymous;
    private LocalDateTime deadline;
    private boolean closed;
    private LocalDateTime createdAt;
    private List<PollOptionDto> options;
}
