package mh.cyb.root.DpiBatchMeetBackend.modules.professional.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BloodGroup;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.BloodDonorProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateBloodDonorRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.service.BloodDonorService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class BloodDonorControllerTest {

    @Mock
    private BloodDonorService bloodDonorService;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private BloodDonorController bloodDonorController;

    private User user;
    private BloodDonorProfileDto profileDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        profileDto = new BloodDonorProfileDto();
        profileDto.setId(1L);
        profileDto.setBloodGroup(BloodGroup.A_POS);
    }

    @Test
    void searchDonors_ShouldReturnList() {
        when(bloodDonorService.searchDonors(any(), any())).thenReturn(Collections.singletonList(profileDto));

        ResponseEntity<List<BloodDonorProfileDto>> response = bloodDonorController.searchDonors(BloodGroup.A_POS,
                "Location");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void registerDonor_ShouldReturnCreated() {
        CreateBloodDonorRequest request = new CreateBloodDonorRequest();
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(bloodDonorService.registerData(any(), any())).thenReturn(profileDto);

        ResponseEntity<BloodDonorProfileDto> response = bloodDonorController.registerDonor(userDetails, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(profileDto, response.getBody());
    }

    @Test
    void getMyProfile_ShouldReturnOk() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(bloodDonorService.getMyProfile(any())).thenReturn(profileDto);

        ResponseEntity<BloodDonorProfileDto> response = bloodDonorController.getMyProfile(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(profileDto, response.getBody());
    }

    @Test
    void updateStatus_ShouldReturnOk() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(bloodDonorService.updateAvailability(true, user)).thenReturn(profileDto);

        ResponseEntity<BloodDonorProfileDto> response = bloodDonorController.updateStatus(userDetails, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateLastDonationDate_ShouldReturnOk() {
        mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateLastDonationRequest request = new mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateLastDonationRequest();
        request.setLastDonationDate(java.time.LocalDate.now());

        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(bloodDonorService.updateLastDonationDate(any(), eq(user))).thenReturn(profileDto);

        ResponseEntity<BloodDonorProfileDto> response = bloodDonorController.updateLastDonationDate(userDetails,
                request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
