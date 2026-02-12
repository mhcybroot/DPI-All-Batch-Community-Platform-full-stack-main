package mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.domain;

import jakarta.persistence.*;
import lombok.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;

@Entity
@Table(name = "knowledge_votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "targetId", "targetType" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteTargetType targetType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType voteType;
}
