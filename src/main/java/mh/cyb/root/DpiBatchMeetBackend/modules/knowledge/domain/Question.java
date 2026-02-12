package mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.domain;

import jakarta.persistence.*;
import lombok.*;
import mh.cyb.root.DpiBatchMeetBackend.common.domain.BaseEntity;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "knowledge_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ElementCollection
    @CollectionTable(name = "question_tags", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    private int viewCount;
    private int upvotes;
    private int downvotes;

    private boolean isSolved;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();
}
