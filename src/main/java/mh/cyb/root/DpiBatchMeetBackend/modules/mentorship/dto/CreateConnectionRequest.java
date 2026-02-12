package mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateConnectionRequest {
    private Long mentorId;
    private String requestMessage;
}
