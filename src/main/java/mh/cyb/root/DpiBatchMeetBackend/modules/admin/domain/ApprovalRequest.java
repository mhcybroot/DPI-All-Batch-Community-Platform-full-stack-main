package mh.cyb.root.DpiBatchMeetBackend.modules.admin.domain;

import jakarta.persistence.*;
import lombok.*;
import mh.cyb.root.DpiBatchMeetBackend.common.domain.BaseEntity;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;

@Entity
@Table(name = "approval_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
