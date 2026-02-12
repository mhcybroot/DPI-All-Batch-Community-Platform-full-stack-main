package mh.cyb.root.DpiBatchMeetBackend.modules.event.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "poll_options")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @Column(name = "option_text", nullable = false)
    private String optionText;

    @Column(name = "vote_count")
    private int voteCount;
}
