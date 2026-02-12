package mh.cyb.root.DpiBatchMeetBackend.modules.community.mapper;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.domain.Notice;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.NoticeDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.CreateNoticeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoticeMapper {
    NoticeDto toDto(Notice notice);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Notice toEntity(CreateNoticeRequest request);
}
