package mh.cyb.root.DpiBatchMeetBackend.modules.community.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticeDto {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private Boolean isPinned;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
