package mh.cyb.root.DpiBatchMeetBackend.modules.admin.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.admin.service.AuditService;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.dto.AuditLogDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuditControllerTest {

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuditController auditController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetLogsByUser() {
        when(auditService.getLogsByActor(1L)).thenReturn(Collections.emptyList());
        ResponseEntity<List<AuditLogDto>> response = auditController
                .getLogsByUser(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(auditService, times(1)).getLogsByActor(1L);
    }
}
