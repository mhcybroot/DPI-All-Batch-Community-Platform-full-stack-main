package mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain;

import jakarta.persistence.*;
import lombok.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;

import java.time.LocalDate;

@Entity
@Table(name = "blood_donor_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodDonorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BloodGroup bloodGroup;

    private LocalDate lastDonationDate;

    @Builder.Default
    private boolean isAvailable = true;

    private String location; // District or City

    private String contactNumber;
}
