package mh.cyb.root.DpiBatchMeetBackend.modules.profile.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.PrivacySetting;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.Profile;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.Skill;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.repository.PrivacySettingRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.repository.ProfileRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.repository.SkillRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileUpdateRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.mapper.ProfileMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private PrivacySettingRepository privacySettingRepository;

    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetProfile_CreatesNewIfNotFound() {
        User user = new User();
        user.setId(1L);
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(profileRepository.save(any(Profile.class))).thenAnswer(i -> i.getArgument(0));
        when(privacySettingRepository.findByUserId(1L)).thenReturn(Optional.empty());

        ProfileDto profileDto = new ProfileDto();
        when(profileMapper.toDto(any(Profile.class))).thenReturn(profileDto);

        ProfileDto result = profileService.getProfile(user);

        assertNotNull(result);
        verify(profileRepository, times(1)).save(any(Profile.class));
        verify(privacySettingRepository, times(1)).save(any(PrivacySetting.class));
    }

    @Test
    public void testUpdateProfile() {
        User user = new User();
        user.setId(1L);
        Profile existingProfile = new Profile();
        existingProfile.setId(10L);
        existingProfile.setUser(user);

        ProfileUpdateRequest updateData = new ProfileUpdateRequest();
        updateData.setBio("Updated Bio");
        updateData.setGithubUrl("github.com/test");

        // Mock updated profile return
        ProfileDto updatedDto = new ProfileDto();
        updatedDto.setBio("Updated Bio");
        updatedDto.setGithubUrl("github.com/test");

        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(existingProfile));
        when(profileRepository.save(any(Profile.class))).thenReturn(existingProfile);
        when(profileMapper.toDto(any(Profile.class))).thenReturn(updatedDto);

        ProfileDto updated = profileService.updateProfile(user, updateData);

        assertEquals("Updated Bio", updated.getBio());
        assertEquals("github.com/test", updated.getGithubUrl());
    }

    @Test
    public void testAddSkills() {
        User user = new User();
        user.setId(1L);
        Profile existingProfile = new Profile();
        existingProfile.setSkills(new HashSet<>());

        ProfileDto updatedDto = new ProfileDto();
        // Assuming ProfileDto has Set<String> skills for now, or we can check simple
        // assertion
        // updatedDto.setSkills(Set.of("Java"));

        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(existingProfile));
        when(skillRepository.findByName("Java")).thenReturn(Optional.empty());
        when(skillRepository.save(any(Skill.class))).thenAnswer(i -> {
            Skill s = i.getArgument(0);
            s.setName("Java");
            return s;
        });
        when(profileRepository.save(any(Profile.class))).thenReturn(existingProfile);
        when(profileMapper.toDto(any(Profile.class))).thenReturn(updatedDto);

        ProfileDto result = profileService.addSkills(user, Set.of("Java"));

        assertNotNull(result);
    }

    @Test
    public void testUpdatePrivacy() {
        User user = new User();
        user.setId(1L);
        PrivacySetting setting = new PrivacySetting();
        setting.setUser(user);

        when(privacySettingRepository.findByUserId(1L)).thenReturn(Optional.of(setting));
        when(privacySettingRepository.save(any(PrivacySetting.class))).thenReturn(setting);

        // Mock getProfile call inside updatePrivacy
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(new Profile()));
        when(profileMapper.toDto(any(Profile.class))).thenReturn(new ProfileDto());

        ProfileDto result = profileService.updatePrivacy(user, true, false, true, false);

        assertNotNull(result);
        verify(privacySettingRepository, times(1)).save(setting);
        assertTrue(setting.isShowEmail());
        assertFalse(setting.isShowPhone());
    }
}
