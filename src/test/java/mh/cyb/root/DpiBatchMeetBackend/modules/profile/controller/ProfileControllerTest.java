package mh.cyb.root.DpiBatchMeetBackend.modules.profile.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.service.ProfileService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private ProfileController profileController;

    private User user;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@example.com");
    }

    @Test
    public void testGetMyProfile() {
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(profileService.getProfile(user)).thenReturn(new ProfileDto());

        ResponseEntity<ProfileDto> response = profileController.getMyProfile(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateMyProfile_ShouldReturnOk() {
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(profileService.updateProfile(eq(user), any())).thenReturn(new ProfileDto());

        ResponseEntity<ProfileDto> response = profileController.updateMyProfile(userDetails, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void addSkills_ShouldReturnOk() {
        Set<String> skills = Set.of("Java", "Spring");
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(profileService.addSkills(eq(user), any())).thenReturn(new ProfileDto());

        ResponseEntity<ProfileDto> response = profileController.addSkills(userDetails, skills);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
