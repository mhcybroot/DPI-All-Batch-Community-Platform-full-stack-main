package mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BloodGroup;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserDto;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloodDonorProfileDto {
    private Long id;
    private UserDto user;
    private BloodGroup bloodGroup;
    private LocalDate lastDonationDate;
    private boolean isAvailable;
    private String location;
    private String contactNumber;
}
