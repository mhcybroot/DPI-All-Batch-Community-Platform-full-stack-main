package mh.cyb.root.DpiBatchMeetBackend.modules.profile.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.Profile;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.repository.ProfileRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.mapper.ProfileMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DirectoryServiceImpl implements DirectoryService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    public DirectoryServiceImpl(ProfileRepository profileRepository,
            ProfileMapper profileMapper) {
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
    }

    @Override
    public List<ProfileDto> searchProfiles(String query) {
        String lowerQuery = query.toLowerCase();
        return profileRepository.findAll().stream()
                .filter(p -> (p.getUser().getFullName() != null
                        && p.getUser().getFullName().toLowerCase().contains(lowerQuery)) ||
                        (p.getBio() != null && p.getBio().toLowerCase().contains(lowerQuery)))
                .map(profileMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<ProfileDto> filterProfiles(String skill, String location,
            String employmentStatus) {
        // Basic implementation for now
        return profileRepository.findAll().stream()
                .map(profileMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }
}
