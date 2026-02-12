package mh.cyb.root.DpiBatchMeetBackend.modules.profile.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.Profile;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.repository.ProfileRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.mapper.ProfileMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DirectoryServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private DirectoryServiceImpl directoryService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchProfiles() {
        User user = new User();
        user.setFullName("John Doe");
        Profile profile = new Profile();
        profile.setUser(user);

        ProfileDto profileDto = new ProfileDto();
        // Set DTO fields

        // Mocking findAll or a custom search method
        when(profileRepository.findAll()).thenReturn(List.of(profile));
        when(profileMapper.toDto(any(Profile.class))).thenReturn(profileDto);

        List<ProfileDto> results = directoryService.searchProfiles("John");
        assertEquals(1, results.size());
    }

    @Test
    public void testFilterProfiles() {
        Profile profile = new Profile();
        when(profileRepository.findAll()).thenReturn(List.of(profile));
        when(profileMapper.toDto(any(Profile.class))).thenReturn(new ProfileDto());

        List<ProfileDto> results = directoryService.filterProfiles(null, null, null);
        assertEquals(1, results.size());
    }
}
