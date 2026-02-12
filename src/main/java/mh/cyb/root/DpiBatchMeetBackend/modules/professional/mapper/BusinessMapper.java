package mh.cyb.root.DpiBatchMeetBackend.modules.professional.mapper;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BusinessProfile;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.BusinessProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateBusinessRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateBusinessRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.mapper.UserMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {
        UserMapper.class }, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class BusinessMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract BusinessProfile toEntity(CreateBusinessRequest request);

    public abstract BusinessProfileDto toDto(BusinessProfile profile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract void updateBusinessFromRequest(UpdateBusinessRequest request,
            @MappingTarget BusinessProfile profile);
}
