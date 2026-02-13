package mh.cyb.root.DpiBatchMeetBackend.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private boolean enabled;
    private boolean accountNonLocked;
    private Set<String> roles;
}
