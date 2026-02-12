package mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BloodGroup;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBloodDonorRequest {
    @NotNull(message = "Blood group is required")
    private BloodGroup bloodGroup;

    private LocalDate lastDonationDate;

    private Boolean isAvailable;

    private String location;

    private String contactNumber;
}
