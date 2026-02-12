package mh.cyb.root.DpiBatchMeetBackend.modules.event.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polls")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(nullable = false)
    private String question;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "is_multiple_choice")
    private boolean multipleChoice;

    @Column(name = "is_anonymous")
    private boolean anonymous;

    private LocalDateTime deadline;

    @Column(name = "is_closed")
    private boolean closed;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PollOption> options = new ArrayList<>();

    public void addOption(PollOption option) {
        options.add(option);
        option.setPoll(this);
    }
}
