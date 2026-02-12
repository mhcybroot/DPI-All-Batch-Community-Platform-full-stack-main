package mh.cyb.root.DpiBatchMeetBackend.modules.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForumStatsDto {
    private long categoryCount;
    private long postCount;
    private long commentCount;
    private long activeUsersCount;
}
