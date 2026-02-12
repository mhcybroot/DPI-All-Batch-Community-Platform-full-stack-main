package mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.domain.ConnectionStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserSummaryDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectionRequestDto {
    private Long id;
    private UserSummaryDto mentee;
    private UserSummaryDto mentor;
    private ConnectionStatus status;
    private String requestMessage;
}
