package mh.cyb.root.DpiBatchMeetBackend.modules.admin.dto;

import lombok.Data;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.domain.ApprovalRequest.ApprovalStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserDto;
import java.time.LocalDateTime;

@Data
public class ApprovalRequestDto {
    private Long id;
    private UserDto user; // Embed UserDto
    private LocalDateTime requestedAt;
    private ApprovalStatus status;
}
