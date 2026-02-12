package mh.cyb.root.DpiBatchMeetBackend.modules.community.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateNoticeRequest {
    private String title;
    private String content;
    private Boolean isPinned;
    private LocalDateTime expiresAt;
}
