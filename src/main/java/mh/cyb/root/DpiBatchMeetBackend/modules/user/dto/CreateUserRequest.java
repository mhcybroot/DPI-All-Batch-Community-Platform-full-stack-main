package mh.cyb.root.DpiBatchMeetBackend.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private String email;
    private String password;
    private String fullName;
    private Set<String> roles; // "ADMINISTRATOR", "MODERATOR", "MEMBER"
}
