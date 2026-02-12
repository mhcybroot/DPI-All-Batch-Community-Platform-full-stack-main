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
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final SkillRepository skillRepository;
    private final PrivacySettingRepository privacySettingRepository;
    private final ProfileMapper profileMapper;

    public ProfileServiceImpl(ProfileRepository profileRepository, SkillRepository skillRepository,
            PrivacySettingRepository privacySettingRepository,
            ProfileMapper profileMapper) {
        this.profileRepository = profileRepository;
        this.skillRepository = skillRepository;
        this.privacySettingRepository = privacySettingRepository;
        this.profileMapper = profileMapper;
    }

    private Profile getProfileEntity(User user) {
        return profileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    // Initialize default profile
                    Profile newProfile = Profile.builder().user(user).build();
                    profileRepository.save(newProfile);

                    // Initialize default privacy settings
                    PrivacySetting privacy = PrivacySetting.builder().user(user).build();
                    privacySettingRepository.save(privacy);

                    return newProfile;
                });
    }

    @Override
    public ProfileDto getProfile(User user) {
        return profileMapper.toDto(getProfileEntity(user));
    }

    @Override
    public ProfileDto updateProfile(User user,
            ProfileUpdateRequest request) {
        Profile profile = getProfileEntity(user);
        profileMapper.updateProfileFromDto(request, profile);
        return profileMapper.toDto(profileRepository.save(profile));
    }

    @Override
    public ProfileDto addSkills(User user, Set<String> skillNames) {
        Profile profile = getProfileEntity(user);
        Set<Skill> skills = profile.getSkills();
        if (skills == null) {
            skills = new java.util.HashSet<>();
            profile.setSkills(skills);
        }

        for (String name : skillNames) {
            String sanitized = name.trim();
            Skill skill = skillRepository.findByName(sanitized)
                    .orElseGet(() -> skillRepository.save(Skill.builder().name(sanitized).build()));
            skills.add(skill);
        }

        return profileMapper.toDto(profileRepository.save(profile));
    }

    @Override
    public ProfileDto updatePrivacy(User user, boolean showEmail, boolean showPhone,
            boolean showLocation,
            boolean showEmployment) {
        PrivacySetting setting = privacySettingRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    PrivacySetting newSetting = PrivacySetting.builder().user(user).build();
                    return privacySettingRepository.save(newSetting);
                });

        setting.setShowEmail(showEmail);
        setting.setShowPhone(showPhone);
        setting.setShowLocation(showLocation);
        setting.setShowEmployment(showEmployment);
        privacySettingRepository.save(setting);

        return getProfile(user);
    }
}
