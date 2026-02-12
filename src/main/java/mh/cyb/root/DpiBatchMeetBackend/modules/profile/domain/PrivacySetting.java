package mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain;

import jakarta.persistence.*;
import lombok.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;

@Entity
@Table(name = "privacy_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrivacySetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    private boolean showEmail = false;

    @Builder.Default
    private boolean showPhone = false;

    @Builder.Default
    private boolean showLocation = true;

    @Builder.Default
    private boolean showEmployment = true;
}
