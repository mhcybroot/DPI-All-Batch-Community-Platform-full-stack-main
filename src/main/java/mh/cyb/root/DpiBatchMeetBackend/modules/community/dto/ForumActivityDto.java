package mh.cyb.root.DpiBatchMeetBackend.modules.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForumActivityDto {
    private Long id;
    private String title;
    private String authorName;
    private String categoryName;
    private LocalDateTime createdAt;
    private String type; // POST or COMMENT
    private String snippet;
}
