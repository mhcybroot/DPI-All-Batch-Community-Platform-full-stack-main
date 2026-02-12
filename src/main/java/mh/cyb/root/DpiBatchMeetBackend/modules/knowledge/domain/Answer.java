package mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.domain;

import jakarta.persistence.*;
import lombok.*;
import mh.cyb.root.DpiBatchMeetBackend.common.domain.BaseEntity;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;

@Entity
@Table(name = "knowledge_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    private boolean isAccepted;

    private int upvotes;
    private int downvotes;
}
