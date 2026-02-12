package mh.cyb.root.DpiBatchMeetBackend.modules.event.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PollOptionDto {
    private Long id;
    private String optionText;
    private int voteCount;
}
