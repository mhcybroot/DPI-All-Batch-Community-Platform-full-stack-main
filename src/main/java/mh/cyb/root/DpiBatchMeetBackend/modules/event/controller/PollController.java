package mh.cyb.root.DpiBatchMeetBackend.modules.event.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.CreatePollRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.PollDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.service.PollService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/polls")
@Tag(name = "Poll & Voting", description = "Endpoints for event polls and voting")
public class PollController {

    private final PollService pollService;
    private final UserService userService;

    public PollController(PollService pollService, UserService userService) {
        this.pollService = pollService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MODERATOR')")
    @Operation(summary = "Create poll", description = "Creates a new poll for an event or general use. Admin/Moderator only.")
    public ResponseEntity<PollDto> createPoll(@RequestBody CreatePollRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return new ResponseEntity<>(pollService.createPoll(request, user), HttpStatus.CREATED);
    }

    @PostMapping("/{pollId}/vote/{optionId}")
    @Operation(summary = "Vote in poll", description = "Submit a vote for a specific option in a poll.")
    public ResponseEntity<Void> vote(@PathVariable Long pollId,
            @PathVariable Long optionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        pollService.vote(pollId, optionId, user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{pollId}/close")
    @Operation(summary = "Close poll", description = "Manually closes a poll. Restricted to creator or admin.")
    public ResponseEntity<Void> closePoll(@PathVariable Long pollId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        pollService.closePoll(pollId, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{pollId}/results")
    @Operation(summary = "Get poll results", description = "Retrieves the current results and vote counts for a poll.")
    public ResponseEntity<PollDto> getPollResults(@PathVariable Long pollId) {
        return ResponseEntity.ok(pollService.getPollResults(pollId));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active polls", description = "Lists all polls that are currently open.")
    public ResponseEntity<List<PollDto>> getActivePolls() {
        return ResponseEntity.ok(pollService.getActivePolls());
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get event polls", description = "Retrieves all polls associated with a specific event.")
    public ResponseEntity<List<PollDto>> getEventPolls(@PathVariable Long eventId) {
        return ResponseEntity.ok(pollService.getEventPolls(eventId));
    }

    private User getUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
