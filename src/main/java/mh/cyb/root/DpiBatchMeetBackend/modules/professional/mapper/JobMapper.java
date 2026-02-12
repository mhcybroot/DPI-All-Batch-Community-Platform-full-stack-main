package mh.cyb.root.DpiBatchMeetBackend.modules.professional.mapper;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobPost;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateJobRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.JobPostDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateJobRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.mapper.UserMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {
        UserMapper.class }, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class JobMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "postedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract JobPost toEntity(CreateJobRequest request);

    public abstract JobPostDto toDto(JobPost jobPost);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "postedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract void updateJobFromRequest(UpdateJobRequest request, @MappingTarget JobPost jobPost);
}
