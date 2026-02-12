package mh.cyb.root.DpiBatchMeetBackend.modules.event.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.EventRegisterRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.RegistrationDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.RejectRegistrationRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.service.RegistrationService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Event Registration", description = "Endpoints for reunion event registration and approval")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserService userService;

    public RegistrationController(RegistrationService registrationService, UserService userService) {
        this.registrationService = registrationService;
        this.userService = userService;
    }

    @PostMapping("/events/{eventId}/register")
    @Operation(summary = "Register for event", description = "Signs up the current user for an event. Status will be PENDING or WAITLISTED.")
    public ResponseEntity<RegistrationDto> register(@PathVariable Long eventId,
            @RequestBody EventRegisterRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return new ResponseEntity<>(registrationService.register(eventId, user, request.getNotes()),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/events/{eventId}/register")
    @Operation(summary = "Cancel registration", description = "Cancels the current user's registration for an event.")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Long eventId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        registrationService.cancelRegistration(eventId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-registrations")
    public ResponseEntity<List<RegistrationDto>> getMyRegistrations(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(registrationService.getUserRegistrations(user));
    }

    @GetMapping("/events/{eventId}/registrations")
    public ResponseEntity<List<RegistrationDto>> getEventRegistrations(@PathVariable Long eventId) {
        return ResponseEntity.ok(registrationService.getEventRegistrations(eventId));
    }

    @GetMapping("/events/{eventId}/registrations/pending")
    public ResponseEntity<List<RegistrationDto>> getPendingRegistrations(@PathVariable Long eventId) {
        return ResponseEntity.ok(registrationService.getPendingRegistrations(eventId));
    }

    @PatchMapping("/registrations/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MODERATOR')")
    @Operation(summary = "Approve registration", description = "Confirms a pending registration. Only admins/moderators.")
    public ResponseEntity<RegistrationDto> approveRegistration(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User admin = getUser(userDetails);
        return ResponseEntity.ok(registrationService.approveRegistration(id, admin));
    }

    @PatchMapping("/registrations/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MODERATOR')")
    @Operation(summary = "Reject registration", description = "Rejects a registration with a reason. Only admins/moderators.")
    public ResponseEntity<RegistrationDto> rejectRegistration(@PathVariable Long id,
            @RequestBody RejectRegistrationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User admin = getUser(userDetails);
        return ResponseEntity.ok(registrationService.rejectRegistration(id, admin, request.getReason()));
    }

    @PatchMapping("/registrations/{id}/mark-attended")
    public ResponseEntity<RegistrationDto> markAsAttended(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User admin = getUser(userDetails);
        return ResponseEntity.ok(registrationService.markAsAttended(id, admin));
    }

    private User getUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
