package mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBusinessRequest {
    @NotBlank(message = "Business name is required")
    private String businessName;

    private String tagline;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Industry is required")
    private String industry;

    private String website;
    private String location;
    private String logoUrl;
    private LocalDate establishedDate;
}
