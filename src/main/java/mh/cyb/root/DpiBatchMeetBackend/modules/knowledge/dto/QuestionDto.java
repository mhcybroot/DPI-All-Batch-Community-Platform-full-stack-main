package mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserSummaryDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDto {
    private Long id;
    private String title;
    private String body;
    private UserSummaryDto author;
    private List<String> tags;
    private int viewCount;
    private int upvotes;
    private int downvotes;
    private boolean isSolved;
    private int answerCount;
    private LocalDateTime createdAt;
}
