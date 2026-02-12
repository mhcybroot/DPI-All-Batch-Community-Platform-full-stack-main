package mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.domain;

import jakarta.persistence.*;
import lombok.*;
import mh.cyb.root.DpiBatchMeetBackend.common.domain.BaseEntity;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentor_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ElementCollection
    @CollectionTable(name = "mentor_expertise", joinColumns = @JoinColumn(name = "mentor_profile_id"))
    @Column(name = "expertise")
    private List<String> expertise = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MentorStatus status; // OPEN, FULL, BUSY

    private int maxMentees;
}
