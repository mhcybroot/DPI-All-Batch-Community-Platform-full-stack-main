package mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.domain.VoteType;

@Data
@NoArgsConstructor
public class VoteRequest {
    private VoteType voteType;
}
