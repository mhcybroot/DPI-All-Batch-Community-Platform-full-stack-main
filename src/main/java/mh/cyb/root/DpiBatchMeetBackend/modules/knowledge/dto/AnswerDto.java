package mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserSummaryDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerDto {
    private Long id;
    private String body;
    private UserSummaryDto author;
    private boolean isAccepted;
    private int upvotes;
    private int downvotes;
    private LocalDateTime createdAt;
}
