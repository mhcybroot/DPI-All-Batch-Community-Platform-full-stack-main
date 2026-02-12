package mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain;

import jakarta.persistence.*;
import lombok.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "business_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String businessName;

    private String tagline;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String industry;

    private String website;

    private String location;

    private String logoUrl;

    private LocalDate establishedDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
