package mh.cyb.root.DpiBatchMeetBackend.modules.admin.mapper;

import mh.cyb.root.DpiBatchMeetBackend.modules.admin.domain.AuditLog;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.dto.AuditLogDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {
    AuditLogDto toDto(AuditLog auditLog);

    AuditLog toEntity(AuditLogDto auditLogDto);
}
