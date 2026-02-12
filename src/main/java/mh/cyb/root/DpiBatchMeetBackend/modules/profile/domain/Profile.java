package mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.common.domain.BaseEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private LocalDate dateOfBirth;

    private String phoneNumber;

    private String linkedInUrl;
    private String githubUrl;
    private String portfolioUrl;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "employment_status_id")
    private EmploymentStatus employmentStatus;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "profile_skills", joinColumns = @JoinColumn(name = "profile_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    @Builder.Default
    private Set<Skill> skills = new HashSet<>();
}
