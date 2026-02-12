package mh.cyb.root.DpiBatchMeetBackend.modules.professional.service;

import lombok.RequiredArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BloodDonorProfile;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BloodGroup;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.BloodDonorProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateBloodDonorRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.mapper.BloodDonorMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.repository.BloodDonorProfileRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BloodDonorServiceImpl implements BloodDonorService {

    private final BloodDonorProfileRepository bloodDonorProfileRepository;
    private final BloodDonorMapper bloodDonorMapper;

    @Override
    public BloodDonorProfileDto registerData(CreateBloodDonorRequest request, User user) {
        Optional<BloodDonorProfile> existingProfile = bloodDonorProfileRepository.findByUser_Id(user.getId());
        BloodDonorProfile profile;
        if (existingProfile.isPresent()) {
            profile = existingProfile.get();
            bloodDonorMapper.updateProfileFromRequest(request, profile);
        } else {
            profile = bloodDonorMapper.toEntity(request);
            profile.setUser(user);
        }
        profile = bloodDonorProfileRepository.save(profile);
        return bloodDonorMapper.toDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public BloodDonorProfileDto getMyProfile(User user) {
        BloodDonorProfile profile = bloodDonorProfileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Donor profile not found"));
        return bloodDonorMapper.toDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloodDonorProfileDto> searchDonors(BloodGroup bloodGroup, String location) {
        List<BloodDonorProfile> profiles;
        if (location != null && !location.isEmpty()) {
            profiles = bloodDonorProfileRepository
                    .findByBloodGroupAndLocationContainingIgnoreCaseAndIsAvailableTrue(bloodGroup, location);
        } else {
            profiles = bloodDonorProfileRepository.findByBloodGroupAndIsAvailableTrue(bloodGroup);
        }
        return profiles.stream().map(bloodDonorMapper::toDto).toList();
    }

    @Override
    public BloodDonorProfileDto updateAvailability(boolean isAvailable, User user) {
        BloodDonorProfile profile = bloodDonorProfileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Donor profile not found"));
        profile.setAvailable(isAvailable);
        profile = bloodDonorProfileRepository.save(profile);
        return bloodDonorMapper.toDto(profile);
    }

    @Override
    public BloodDonorProfileDto updateLastDonationDate(java.time.LocalDate lastDonationDate, User user) {
        BloodDonorProfile profile = bloodDonorProfileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Donor profile not found"));
        profile.setLastDonationDate(lastDonationDate);
        profile = bloodDonorProfileRepository.save(profile);
        return bloodDonorMapper.toDto(profile);
    }
}
