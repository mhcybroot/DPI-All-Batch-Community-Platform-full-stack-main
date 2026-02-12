package mh.cyb.root.DpiBatchMeetBackend.modules.professional.mapper;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BloodDonorProfile;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.BloodDonorProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateBloodDonorRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.mapper.UserMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {
                UserMapper.class }, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class BloodDonorMapper {

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "user", ignore = true)
        @Mapping(target = "isAvailable", source = "isAvailable")
        public abstract BloodDonorProfile toEntity(CreateBloodDonorRequest request);

        @Mapping(target = "isAvailable", source = "available")
        public abstract BloodDonorProfileDto toDto(BloodDonorProfile profile);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "user", ignore = true)
        @Mapping(target = "available", source = "isAvailable")
        public abstract void updateProfileFromRequest(CreateBloodDonorRequest request,
                        @MappingTarget BloodDonorProfile profile);
}
