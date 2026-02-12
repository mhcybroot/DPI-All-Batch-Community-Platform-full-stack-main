package mh.cyb.root.DpiBatchMeetBackend.modules.admin.mapper;

import mh.cyb.root.DpiBatchMeetBackend.modules.admin.domain.ApprovalRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.dto.ApprovalRequestDto;
import org.mapstruct.Mapper;

import mh.cyb.root.DpiBatchMeetBackend.modules.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface ApprovalRequestMapper {
    @org.mapstruct.Mapping(source = "createdAt", target = "requestedAt")
    ApprovalRequestDto toDto(ApprovalRequest request);
}
