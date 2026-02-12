package mh.cyb.root.DpiBatchMeetBackend.modules.web.event;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.EventStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.CreateEventRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.UpdateEventRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.service.EventService;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.service.RegistrationService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/events")
public class EventViewController {

    private final EventService eventService;
    private final RegistrationService registrationService;
    private final UserService userService;

    public EventViewController(EventService eventService, RegistrationService registrationService,
            UserService userService) {
        this.eventService = eventService;
        this.registrationService = registrationService;
        this.userService = userService;
    }

    @GetMapping("")
    public String listEvents(Model model) {
        model.addAttribute("events", eventService.getUpcomingEvents());
        model.addAttribute("activeNav", "events");
        return "events/index";
    }

    @GetMapping("/{id:[0-9]+}")
    public String eventDetail(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("event", eventService.getEventById(id));
        model.addAttribute("isRegistered", registrationService.isUserRegistered(id, user.getId()));
        model.addAttribute("attendeeCount", registrationService.getApprovedCount(id));
        model.addAttribute("activeNav", "events");
        return "events/detail";
    }

    @GetMapping("/new")
    public String newEventForm(Model model) {
        model.addAttribute("activeNav", "events");
        return "events/new";
    }

    @PostMapping("")
    public String createEvent(@ModelAttribute CreateEventRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        eventService.createEvent(request, user);
        redirectAttributes.addFlashAttribute("successMessage", "Event created!");
        return "redirect:/web/events";
    }

    @PostMapping("/{id:[0-9]+}/register")
    public String registerForEvent(@PathVariable Long id,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        registrationService.register(id, user, notes);
        redirectAttributes.addFlashAttribute("successMessage", "You have registered for this event!");
        return "redirect:/web/events/" + id;
    }

    @PostMapping("/{id:[0-9]+}/cancel-registration")
    public String cancelRegistration(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        registrationService.cancelRegistration(id, user);
        redirectAttributes.addFlashAttribute("successMessage", "Registration cancelled.");
        return "redirect:/web/events/" + id;
    }

    @PostMapping("/{id:[0-9]+}/delete")
    public String deleteEvent(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        eventService.deleteEvent(id, user);
        redirectAttributes.addFlashAttribute("successMessage", "Event deleted.");
        return "redirect:/web/events";
    }

    // --- Phase B: New endpoints ---

    @GetMapping("/{id:[0-9]+}/edit")
    public String editEventForm(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getEventById(id));
        model.addAttribute("activeNav", "events");
        return "events/edit";
    }

    @PostMapping("/{id:[0-9]+}/edit")
    public String updateEvent(@PathVariable Long id, @ModelAttribute UpdateEventRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        eventService.updateEvent(id, request, user);
        redirectAttributes.addFlashAttribute("successMessage", "Event updated!");
        return "redirect:/web/events/" + id;
    }

    @PostMapping("/{id:[0-9]+}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam EventStatus status,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        eventService.updateStatus(id, status, user);
        redirectAttributes.addFlashAttribute("successMessage", "Event status updated.");
        return "redirect:/web/events/" + id;
    }

    @GetMapping("/my-events")
    public String myEvents(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("events", eventService.getEventsByOrganizer(user.getId()));
        model.addAttribute("activeNav", "events");
        return "events/my-events";
    }

    @GetMapping("/my-registrations")
    public String myRegistrations(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("registrations", registrationService.getUserRegistrations(user));
        model.addAttribute("activeNav", "events");
        return "events/my-registrations";
    }

    @GetMapping("/{eventId:[0-9]+}/registrations")
    public String eventRegistrations(@PathVariable Long eventId,
            @RequestParam(required = false) String status,
            Model model) {
        List<RegistrationDto> registrations = registrationService.getEventRegistrations(eventId);

        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("ALL")) {
            registrations = registrations.stream()
                    .filter(r -> r.getStatus().name().equalsIgnoreCase(status))
                    .toList();
        }

        model.addAttribute("event", eventService.getEventById(eventId));
        model.addAttribute("registrations", registrations);
        model.addAttribute("pendingRegistrations", registrationService.getPendingRegistrations(eventId));
        model.addAttribute("activeNav", "events");
        model.addAttribute("currentStatus", status != null ? status : "ALL");
        return "events/registrations";
    }

    @PostMapping("/registrations/{id}/approve")
    public String approveRegistration(@PathVariable Long id,
            @RequestParam Long eventId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        registrationService.approveRegistration(id, user);
        redirectAttributes.addFlashAttribute("successMessage", "Registration approved!");
        return "redirect:/web/events/" + eventId + "/registrations";
    }

    @PostMapping("/registrations/{id}/reject")
    public String rejectRegistration(@PathVariable Long id,
            @RequestParam Long eventId,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        registrationService.rejectRegistration(id, user, reason);
        redirectAttributes.addFlashAttribute("successMessage", "Registration rejected.");
        return "redirect:/web/events/" + eventId + "/registrations";
    }

    @PostMapping("/registrations/{id}/mark-attended")
    public String markAttended(@PathVariable Long id,
            @RequestParam Long eventId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        registrationService.markAsAttended(id, user);
        redirectAttributes.addFlashAttribute("successMessage", "Marked as attended.");
        return "redirect:/web/events/" + eventId + "/registrations";
    }

    @GetMapping("/organizer/{organizerId}")
    public String organizerEvents(@PathVariable Long organizerId, Model model) {
        model.addAttribute("events", eventService.getEventsByOrganizer(organizerId));
        // We might want to fetch the organizer's name too, but lets start with the
        // events
        model.addAttribute("organizerId", organizerId);
        model.addAttribute("activeNav", "events");
        return "events/organizer";
    }
}
