package mh.cyb.root.DpiBatchMeetBackend.modules.community.dto;

import lombok.Data;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.domain.MediaType;
import java.time.LocalDateTime;

@Data
public class MemoryDto {
    private Long id;
    private String title;
    private String description;
    private String mediaUrl;
    private MediaType mediaType;
    private Long uploaderId;
    private LocalDateTime createdAt;
}
