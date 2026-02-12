package mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserDto;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessProfileDto {
    private Long id;
    private UserDto owner;
    private String businessName;
    private String tagline;
    private String description;
    private String industry;
    private String website;
    private String location;
    private String logoUrl;
    private LocalDate establishedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
