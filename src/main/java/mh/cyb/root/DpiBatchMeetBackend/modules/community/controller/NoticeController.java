package mh.cyb.root.DpiBatchMeetBackend.modules.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.NoticeDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.CreateNoticeRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.service.NoticeService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import mh.cyb.root.DpiBatchMeetBackend.common.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@Tag(name = "Notice Board", description = "Endpoints for community notices and announcements")
public class NoticeController {

    private final NoticeService noticeService;
    private final UserService userService;

    public NoticeController(NoticeService noticeService, UserService userService) {
        this.noticeService = noticeService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all notices", description = "Retrieves all active (non-expired) notices, sorted by pinned status and creation date.")
    public ResponseEntity<List<NoticeDto>> getAllNotices() {
        return ResponseEntity.ok(noticeService.getAllActiveNotices());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Create a notice", description = "Creates a new notice. Requires ADMINISTRATOR role.")
    public ResponseEntity<NoticeDto> createNotice(@RequestBody CreateNoticeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.status(201).body(noticeService.createNotice(request, user.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Update a notice", description = "Updates an existing notice. Requires ADMINISTRATOR role.")
    public ResponseEntity<NoticeDto> updateNotice(@PathVariable Long id, @RequestBody CreateNoticeRequest request) {
        return ResponseEntity.ok(noticeService.updateNotice(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Delete a notice", description = "Deletes a notice. Requires ADMINISTRATOR role.")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }
}
