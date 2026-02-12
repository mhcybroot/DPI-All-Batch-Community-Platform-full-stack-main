package mh.cyb.root.DpiBatchMeetBackend.modules.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.domain.ApprovalRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.CreateUserRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.dto.ApprovalRequestDto;
import mh.cyb.root.DpiBatchMeetBackend.common.exception.ResourceNotFoundException;
import mh.cyb.root.DpiBatchMeetBackend.modules.admin.service.ApprovalService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMINISTRATOR')")
@Tag(name = "Admin Management", description = "Endpoints for Administrators to manage approvals and users. Requires Role: ADMINISTRATOR")
public class AdminController {

    private final ApprovalService approvalService;
    private final UserService userService;

    public AdminController(ApprovalService approvalService, UserService userService) {
        this.approvalService = approvalService;
        this.userService = userService;
    }

    @GetMapping("/approvals")
    @Operation(summary = "Get Pending Approvals", description = "Retrieves a list of all users waiting for approval. Requires Role: ADMINISTRATOR.")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    public ResponseEntity<List<ApprovalRequestDto>> getPendingApprovals() {
        return ResponseEntity.ok(approvalService.getPendingRequests());
    }

    @PostMapping("/approvals/{id}/approve")
    @Operation(summary = "Approve User", description = "Approves a pending registration request. Requires Role: ADMINISTRATOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User approved successfully"),
            @ApiResponse(responseCode = "404", description = "Reviewer or Request not found")
    })
    public ResponseEntity<?> approveUser(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User reviewer = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));
        return ResponseEntity.ok(approvalService.approveRequest(id, reviewer));
    }

    @PostMapping("/users")
    @Operation(summary = "Create User", description = "Creates a new user with specific roles. Requires Role: ADMINISTRATOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or email in use")
    })
    public ResponseEntity<UserDto> createUser(
            @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(201).body(userService.createUser(request));
    }

    @PostMapping("/approvals/{id}/reject")
    @Operation(summary = "Reject User", description = "Rejects a pending registration request. Requires Role: ADMINISTRATOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Reviewer or Request not found")
    })
    public ResponseEntity<?> rejectUser(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody(required = false) String reason) {
        User reviewer = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));
        return ResponseEntity.ok(approvalService.rejectRequest(id, reviewer, reason));
    }
}
