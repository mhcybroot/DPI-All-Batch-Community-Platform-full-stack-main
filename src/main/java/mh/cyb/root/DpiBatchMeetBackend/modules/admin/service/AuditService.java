package mh.cyb.root.DpiBatchMeetBackend.modules.admin.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.admin.dto.AuditLogDto;
import java.util.List;

public interface AuditService {
    void logAction(Long actorId, String action, String targetId, String details, String ipAddress);

    List<AuditLogDto> getLogsByActor(Long actorId);
}
