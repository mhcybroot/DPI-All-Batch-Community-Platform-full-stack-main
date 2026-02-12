package mh.cyb.root.DpiBatchMeetBackend.modules.profile.mapper;

import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.Profile;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileUpdateRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.mapper.UserMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.mapper.SkillMapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = { UserMapper.class, SkillMapper.class })
public interface ProfileMapper {

    @Mapping(source = "location.city", target = "locationCity")
    @Mapping(source = "location.country", target = "locationCountry")
    @Mapping(source = "employmentStatus.statusName", target = "employmentStatus")
    ProfileDto toDto(Profile profile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "locationCity", target = "location.city")
    @Mapping(source = "locationCountry", target = "location.country")
    @Mapping(source = "employmentStatus", target = "employmentStatus.statusName")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateProfileFromDto(ProfileUpdateRequest request, @MappingTarget Profile profile);
}
