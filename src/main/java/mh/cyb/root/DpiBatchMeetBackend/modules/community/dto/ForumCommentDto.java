package mh.cyb.root.DpiBatchMeetBackend.modules.community.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ForumCommentDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long postId;
    private LocalDateTime createdAt;
}
