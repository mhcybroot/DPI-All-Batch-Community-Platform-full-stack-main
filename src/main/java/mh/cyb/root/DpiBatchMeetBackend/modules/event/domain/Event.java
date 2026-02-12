package mh.cyb.root.DpiBatchMeetBackend.modules.event.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    private String venue;

    @Column(name = "venue_address", columnDefinition = "TEXT")
    private String venueAddress;

    @Column(name = "organizer_id", nullable = false)
    private Long organizerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EventStatus status = EventStatus.UPCOMING;

    @Column(name = "max_attendees")
    private Integer maxAttendees;

    @Column(name = "registration_deadline")
    private LocalDateTime registrationDeadline;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
