package mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UpdateLastDonationRequest {
    private LocalDate lastDonationDate;
}
