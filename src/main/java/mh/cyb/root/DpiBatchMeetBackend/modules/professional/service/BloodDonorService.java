package mh.cyb.root.DpiBatchMeetBackend.modules.professional.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BloodGroup;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.BloodDonorProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateBloodDonorRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;

import java.util.List;

public interface BloodDonorService {
    BloodDonorProfileDto registerData(CreateBloodDonorRequest request, User user);

    BloodDonorProfileDto getMyProfile(User user);

    List<BloodDonorProfileDto> searchDonors(BloodGroup bloodGroup, String location);

    BloodDonorProfileDto updateAvailability(boolean isAvailable, User user);

    BloodDonorProfileDto updateLastDonationDate(java.time.LocalDate lastDonationDate, User user);
}
