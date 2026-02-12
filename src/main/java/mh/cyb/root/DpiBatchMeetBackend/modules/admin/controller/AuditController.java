package mh.cyb.root.DpiBatchMeetBackend.modules.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import mh.cyb.root.DpiBatchMeetBackend.modules.admin.service.AuditService;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.dto.AuditLogDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
@PreAuthorize("hasRole('ADMINISTRATOR')")
@Tag(name = "Audit Logs", description = "Endpoints for viewing system audit logs. Requires Role: ADMINISTRATOR")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get User Logs", description = "Retrieves audit logs associated with a specific user actor. Requires Role: ADMINISTRATOR.")
    @ApiResponse(responseCode = "200", description = "Logs retrieved successfully")
    public ResponseEntity<List<AuditLogDto>> getLogsByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(auditService.getLogsByActor(userId));
    }
}
