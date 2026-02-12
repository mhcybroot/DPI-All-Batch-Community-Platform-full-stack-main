package mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employment_statuses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmploymentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String statusName; // e.g., "Full-time", "Student", "Looking for work"
}
