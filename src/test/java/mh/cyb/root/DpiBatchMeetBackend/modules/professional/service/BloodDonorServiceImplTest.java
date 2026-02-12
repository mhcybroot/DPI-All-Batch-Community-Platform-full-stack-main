package mh.cyb.root.DpiBatchMeetBackend.modules.professional.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BloodDonorProfile;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BloodGroup;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.BloodDonorProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateBloodDonorRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.mapper.BloodDonorMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.repository.BloodDonorProfileRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BloodDonorServiceImplTest {

    @Mock
    private BloodDonorProfileRepository bloodDonorProfileRepository;

    @Mock
    private BloodDonorMapper bloodDonorMapper;

    @InjectMocks
    private BloodDonorServiceImpl bloodDonorService;

    private User user;
    private BloodDonorProfile profile;
    private BloodDonorProfileDto profileDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);

        profile = new BloodDonorProfile();
        profile.setId(1L);
        profile.setUser(user);
        profile.setBloodGroup(BloodGroup.A_POS);
        profile.setAvailable(true); // Using setter

        profileDto = new BloodDonorProfileDto();
        profileDto.setId(1L);
        profileDto.setBloodGroup(BloodGroup.A_POS);
    }

    @Test
    void registerData_ShouldCreateNewProfile() {
        CreateBloodDonorRequest request = new CreateBloodDonorRequest();
        request.setBloodGroup(BloodGroup.A_POS);

        when(bloodDonorProfileRepository.findByUser_Id(1L)).thenReturn(Optional.empty());
        when(bloodDonorMapper.toEntity(request)).thenReturn(profile);
        when(bloodDonorProfileRepository.save(any())).thenReturn(profile);
        when(bloodDonorMapper.toDto(profile)).thenReturn(profileDto);

        BloodDonorProfileDto result = bloodDonorService.registerData(request, user);

        assertNotNull(result);
        verify(bloodDonorProfileRepository).save(any());
    }

    @Test
    void registerData_ShouldUpdateExistingProfile() {
        CreateBloodDonorRequest request = new CreateBloodDonorRequest();
        when(bloodDonorProfileRepository.findByUser_Id(1L)).thenReturn(Optional.of(profile));
        doNothing().when(bloodDonorMapper).updateProfileFromRequest(request, profile);
        when(bloodDonorProfileRepository.save(profile)).thenReturn(profile);
        when(bloodDonorMapper.toDto(profile)).thenReturn(profileDto);

        BloodDonorProfileDto result = bloodDonorService.registerData(request, user);

        assertNotNull(result);
        verify(bloodDonorMapper).updateProfileFromRequest(request, profile);
    }

    @Test
    void searchDonors_ShouldReturnList() {
        when(bloodDonorProfileRepository.findByBloodGroupAndIsAvailableTrue(BloodGroup.A_POS))
                .thenReturn(Collections.singletonList(profile));
        when(bloodDonorMapper.toDto(profile)).thenReturn(profileDto);

        List<BloodDonorProfileDto> result = bloodDonorService.searchDonors(BloodGroup.A_POS, null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void updateAvailability_ShouldUpdateStatus() {
        when(bloodDonorProfileRepository.findByUser_Id(1L)).thenReturn(Optional.of(profile));
        when(bloodDonorProfileRepository.save(profile)).thenReturn(profile);
        when(bloodDonorMapper.toDto(profile)).thenReturn(profileDto);

        BloodDonorProfileDto result = bloodDonorService.updateAvailability(false, user);

        assertNotNull(result);
        verify(bloodDonorProfileRepository).save(profile);
    }

    @Test
    void updateLastDonationDate_ShouldUpdateDate() {
        java.time.LocalDate newDate = java.time.LocalDate.now();
        when(bloodDonorProfileRepository.findByUser_Id(1L)).thenReturn(Optional.of(profile));
        when(bloodDonorProfileRepository.save(profile)).thenReturn(profile);
        when(bloodDonorMapper.toDto(profile)).thenReturn(profileDto);

        BloodDonorProfileDto result = bloodDonorService.updateLastDonationDate(newDate, user);

        assertNotNull(result);
        verify(bloodDonorProfileRepository).save(profile);
        assertEquals(newDate, profile.getLastDonationDate());
    }

    @Test
    void getMyProfile_ShouldReturnProfile() {
        when(bloodDonorProfileRepository.findByUser_Id(1L)).thenReturn(Optional.of(profile));
        when(bloodDonorMapper.toDto(profile)).thenReturn(profileDto);

        BloodDonorProfileDto result = bloodDonorService.getMyProfile(user);

        assertNotNull(result);
        assertEquals(profileDto.getId(), result.getId());
    }
}
