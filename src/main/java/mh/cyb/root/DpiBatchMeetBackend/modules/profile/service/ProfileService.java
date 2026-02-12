package mh.cyb.root.DpiBatchMeetBackend.modules.profile.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.Profile;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import java.util.Optional;
import java.util.Set;

import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileUpdateRequest;

public interface ProfileService {
    ProfileDto getProfile(User user);

    ProfileDto updateProfile(User user, ProfileUpdateRequest request);

    ProfileDto addSkills(User user, Set<String> skillNames);

    ProfileDto updatePrivacy(User user, boolean showEmail, boolean showPhone, boolean showLocation,
            boolean showEmployment);
}
