package mh.cyb.root.DpiBatchMeetBackend.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryDto {
    private Long id;
    private String name;
    private String profilePictureUrl; // Optional, can add more fields if needed
}
