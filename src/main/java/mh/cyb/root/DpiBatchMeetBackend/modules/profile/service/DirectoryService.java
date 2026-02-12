package mh.cyb.root.DpiBatchMeetBackend.modules.profile.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileDto;
import java.util.List;

public interface DirectoryService {
    List<ProfileDto> searchProfiles(String query);

    List<ProfileDto> filterProfiles(String skill, String location, String employmentStatus);
}
