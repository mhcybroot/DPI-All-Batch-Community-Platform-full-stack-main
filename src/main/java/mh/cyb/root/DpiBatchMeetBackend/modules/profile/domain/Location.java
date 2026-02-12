package mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;
    private String country;

    @Column(name = "full_address")
    private String fullAddress;
}
