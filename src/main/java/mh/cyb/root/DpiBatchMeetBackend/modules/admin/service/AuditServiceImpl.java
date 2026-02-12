package mh.cyb.root.DpiBatchMeetBackend.modules.admin.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.admin.domain.AuditLog;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.repository.AuditLogRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.dto.AuditLogDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.mapper.AuditLogMapper;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    public AuditServiceImpl(AuditLogRepository auditLogRepository,
            AuditLogMapper auditLogMapper) {
        this.auditLogRepository = auditLogRepository;
        this.auditLogMapper = auditLogMapper;
    }

    @Override
    public void logAction(Long actorId, String action, String targetId, String details, String ipAddress) {
        AuditLog log = AuditLog.builder()
                .actorId(actorId)
                .action(action)
                .targetId(targetId)
                .details(details)
                .ipAddress(ipAddress)
                .build();
        auditLogRepository.save(log);
    }

    @Override
    public List<AuditLogDto> getLogsByActor(Long actorId) {
        return auditLogRepository.findByActorId(actorId).stream()
                .map(auditLogMapper::toDto)
                .collect(Collectors.toList());
    }
}
