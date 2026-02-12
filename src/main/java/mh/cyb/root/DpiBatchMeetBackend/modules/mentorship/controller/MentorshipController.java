package mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.domain.ConnectionStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.service.MentorshipService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentorship")
@RequiredArgsConstructor
@Tag(name = "Mentorship Module", description = "Mentorship connections and searches")
public class MentorshipController {

    private final MentorshipService mentorshipService;
    private final UserService userService;

    @PostMapping("/mentors/register")
    @Operation(summary = "Register/Update as Mentor")
    public ResponseEntity<MentorProfileDto> registerMentor(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody RegisterMentorRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(mentorshipService.registerAsMentor(request, user));
    }

    @GetMapping("/mentors")
    @Operation(summary = "Search Mentors")
    public ResponseEntity<Page<MentorProfileDto>> searchMentors(
            @RequestParam(required = false) String expertise,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(mentorshipService.searchMentors(expertise, pageable));
    }

    @PostMapping("/connect")
    @Operation(summary = "Send Mentorship Request")
    public ResponseEntity<ConnectionRequestDto> sendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateConnectionRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(mentorshipService.sendRequest(request, user));
    }

    @GetMapping("/requests/outgoing")
    @Operation(summary = "Get My Sent Requests (As Mentee)")
    public ResponseEntity<List<ConnectionRequestDto>> getMyRequests(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(mentorshipService.getMyRequests(user));
    }

    @GetMapping("/requests/incoming")
    @Operation(summary = "Get Incoming Requests (As Mentor)")
    public ResponseEntity<List<ConnectionRequestDto>> getIncomingRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(mentorshipService.getIncomingRequests(user));
    }

    @PatchMapping("/requests/{id}")
    @Operation(summary = "Update Request Status (Accept/Reject)")
    public ResponseEntity<ConnectionRequestDto> updateStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam ConnectionStatus status) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(mentorshipService.updateRequestStatus(id, status, user));
    }

    private User getUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
