package mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBusinessRequest {
    private String businessName;
    private String tagline;
    private String description;
    private String industry;
    private String website;
    private String location;
    private String logoUrl;
    private LocalDate establishedDate;
}
