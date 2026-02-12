package mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.domain;

import jakarta.persistence.*;
import lombok.*;
import mh.cyb.root.DpiBatchMeetBackend.common.domain.BaseEntity;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;

@Entity
@Table(name = "mentorship_connections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorshipConnection extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    private User mentee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectionStatus status;

    @Column(columnDefinition = "TEXT")
    private String requestMessage;
}
