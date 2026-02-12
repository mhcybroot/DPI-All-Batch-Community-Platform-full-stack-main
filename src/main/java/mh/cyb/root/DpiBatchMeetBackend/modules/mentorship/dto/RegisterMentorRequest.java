package mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.domain.MentorStatus;

import java.util.List;

@Data
@NoArgsConstructor
public class RegisterMentorRequest {
    private List<String> expertise;
    private int maxMentees;
    private MentorStatus status;
}
