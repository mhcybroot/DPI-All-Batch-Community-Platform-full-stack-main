package mh.cyb.root.DpiBatchMeetBackend.modules.community.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "forum_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon_url")
    private String iconUrl;
}
