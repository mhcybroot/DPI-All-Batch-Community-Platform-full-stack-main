package mh.cyb.root.DpiBatchMeetBackend.modules.community.mapper;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.domain.Memory;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.MemoryDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.UploadMemoryRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemoryMapper {
    MemoryDto toDto(Memory memory);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uploaderId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Memory toEntity(UploadMemoryRequest request);
}
