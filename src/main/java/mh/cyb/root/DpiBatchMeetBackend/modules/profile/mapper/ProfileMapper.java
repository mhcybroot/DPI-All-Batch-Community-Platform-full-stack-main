package mh.cyb.root.DpiBatchMeetBackend.modules.profile.mapper;

import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.EmploymentStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.Location;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.Profile;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileUpdateRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.mapper.UserMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.mapper.SkillMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.repository.EmploymentStatusRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.repository.LocationRepository;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = { UserMapper.class, SkillMapper.class })
public abstract class ProfileMapper {

    @Autowired
    private EmploymentStatusRepository employmentStatusRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Mapping(source = "location.city", target = "locationCity")
    @Mapping(source = "location.country", target = "locationCountry")
    @Mapping(source = "employmentStatus.statusName", target = "employmentStatus")
    public abstract ProfileDto toDto(Profile profile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "employmentStatus", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract void updateProfileFromDto(ProfileUpdateRequest request, @MappingTarget Profile profile);

    @AfterMapping
    protected void handleNestedEntities(ProfileUpdateRequest request, @MappingTarget Profile profile) {
        // Handle Location
        if (request.getLocationCity() != null || request.getLocationCountry() != null) {
            Location location = profile.getLocation();
            if (location == null) {
                location = new Location();
            }
            if (request.getLocationCity() != null) {
                location.setCity(request.getLocationCity());
            }
            if (request.getLocationCountry() != null) {
                location.setCountry(request.getLocationCountry());
            }
            profile.setLocation(locationRepository.save(location));
        }

        // Handle EmploymentStatus
        if (request.getEmploymentStatus() != null && !request.getEmploymentStatus().trim().isEmpty()) {
            EmploymentStatus status = employmentStatusRepository
                    .findByStatusName(request.getEmploymentStatus())
                    .orElseGet(() -> employmentStatusRepository
                            .save(EmploymentStatus.builder().statusName(request.getEmploymentStatus()).build()));
            profile.setEmploymentStatus(status);
        }
    }
}
