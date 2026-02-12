package mh.cyb.root.DpiBatchMeetBackend.modules.admin.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.admin.domain.AuditLog;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.repository.AuditLogRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.mapper.AuditLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuditServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AuditLogMapper auditLogMapper;

    @InjectMocks
    private AuditServiceImpl auditService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogAction() {
        auditService.logAction(1L, "LOGIN", "session-1", "User logged in", "127.0.0.1");

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    public void testGetLogsByActor() {
        AuditLog log = new AuditLog();
        log.setActorId(1L);

        when(auditLogRepository.findByActorId(1L)).thenReturn(List.of(log));
        when(auditLogMapper.toDto(any(AuditLog.class)))
                .thenReturn(new mh.cyb.root.DpiBatchMeetBackend.modules.admin.dto.AuditLogDto());

        List<mh.cyb.root.DpiBatchMeetBackend.modules.admin.dto.AuditLogDto> results = auditService.getLogsByActor(1L);

        assertEquals(1, results.size());
    }
}
