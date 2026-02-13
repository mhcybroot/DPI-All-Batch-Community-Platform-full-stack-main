package mh.cyb.root.DpiBatchMeetBackend.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String fullName;
    private Set<Role> roles;
    private boolean enabled;
    private boolean accountNonLocked;
    private LocalDateTime createdAt;
}
