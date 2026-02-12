package mh.cyb.root.DpiBatchMeetBackend.modules.event.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "votes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_option_id", nullable = false)
    private PollOption pollOption;

    @Column(name = "voter_id", nullable = false)
    private Long voterId;

    @CreationTimestamp
    @Column(name = "voted_at", nullable = false, updatable = false)
    private LocalDateTime votedAt;
}
