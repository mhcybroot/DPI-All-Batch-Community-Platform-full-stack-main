package mh.cyb.root.DpiBatchMeetBackend.modules.community.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ForumPostDto {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
