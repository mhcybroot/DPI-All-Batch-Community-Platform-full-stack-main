package mh.cyb.root.DpiBatchMeetBackend.modules.event.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.EventStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.service.EventService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Event Management", description = "Endpoints for managing reunion events")
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get upcoming events", description = "Retrieves a list of upcoming reunion events.")
    public ResponseEntity<List<EventSummaryDto>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID", description = "Retrieves technical details for a specific event.")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MODERATOR')")
    @Operation(summary = "Create event", description = "Creates a new event. Restricted to admins and moderators.")
    public ResponseEntity<EventDto> createEvent(@RequestBody CreateEventRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new ResponseEntity<>(eventService.createEvent(request, user),
                org.springframework.http.HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update event", description = "Updates an existing event. Only organizer or admin can update.")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long id,
            @RequestBody UpdateEventRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(eventService.updateEvent(id, request, user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Delete event", description = "Permanently deletes an event. Admin only.")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        eventService.deleteEvent(id, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MODERATOR')")
    @Operation(summary = "Update event status", description = "Changes event state (e.g., UPCOMING -> ONGOING).")
    public ResponseEntity<EventDto> updateStatus(@PathVariable Long id,
            @RequestParam EventStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(eventService.updateStatus(id, status, user));
    }

    @GetMapping("/organizer/{organizerId}")
    @Operation(summary = "Get events by organizer", description = "Retrieves all events organized by a specific user.")
    public ResponseEntity<List<EventSummaryDto>> getEventsByOrganizer(@PathVariable Long organizerId) {
        return ResponseEntity.ok(eventService.getEventsByOrganizer(organizerId));
    }
}
