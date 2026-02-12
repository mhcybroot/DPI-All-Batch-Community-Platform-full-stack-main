package mh.cyb.root.DpiBatchMeetBackend.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogDto {
    private Long id;
    private LocalDateTime timestamp;
    private Long actorId;
    private String action;
    private String targetId;
    private String details;
    private String ipAddress;
}
