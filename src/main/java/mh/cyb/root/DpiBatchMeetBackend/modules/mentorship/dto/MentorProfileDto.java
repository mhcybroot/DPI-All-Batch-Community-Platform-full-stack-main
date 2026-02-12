package mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.domain.MentorStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserSummaryDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfileDto {
    private Long id;
    private UserSummaryDto user;
    private List<String> expertise;
    private MentorStatus status;
    private int maxMentees;
}
