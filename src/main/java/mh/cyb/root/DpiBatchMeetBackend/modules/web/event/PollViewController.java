package mh.cyb.root.DpiBatchMeetBackend.modules.web.event;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.CreatePollRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.service.PollService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/web/polls")
public class PollViewController {

    private final PollService pollService;
    private final UserService userService;
    private final mh.cyb.root.DpiBatchMeetBackend.modules.event.service.EventService eventService;

    public PollViewController(PollService pollService, UserService userService,
            mh.cyb.root.DpiBatchMeetBackend.modules.event.service.EventService eventService) {
        this.pollService = pollService;
        this.userService = userService;
        this.eventService = eventService;
    }

    @GetMapping("")
    public String listPolls(Model model) {
        model.addAttribute("polls", pollService.getActivePolls());
        model.addAttribute("activeNav", "events");
        return "polls/index";
    }

    @GetMapping("/{id:[0-9]+}")
    public String pollDetail(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("poll", pollService.getPollResults(id));
        model.addAttribute("hasVoted", pollService.hasUserVoted(id, user.getId()));
        model.addAttribute("activeNav", "events");
        return "polls/detail";
    }

    @PostMapping("/{pollId:[0-9]+}/vote")
    public String vote(@PathVariable Long pollId,
            @RequestParam Long optionId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
            pollService.vote(pollId, optionId, user);
            redirectAttributes.addFlashAttribute("successMessage", "Vote submitted!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/web/polls/" + pollId;
    }

    @GetMapping("/create")
    public String createPollForm(Model model) {
        model.addAttribute("upcomingEvents", eventService.getUpcomingEvents());
        model.addAttribute("activeNav", "events");
        return "polls/create";
    }

    @PostMapping("")
    public String createPoll(@RequestParam String question,
            @RequestParam List<String> options,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false, defaultValue = "false") boolean multipleChoice,
            @RequestParam(required = false, defaultValue = "false") boolean anonymous,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        CreatePollRequest request = new CreatePollRequest();
        request.setQuestion(question);
        request.setOptions(options);
        request.setEventId(eventId);
        request.setMultipleChoice(multipleChoice);
        request.setAnonymous(anonymous);
        pollService.createPoll(request, user);
        redirectAttributes.addFlashAttribute("successMessage", "Poll created!");
        return "redirect:/web/polls";
    }

    @PostMapping("/{pollId:[0-9]+}/close")
    public String closePoll(@PathVariable Long pollId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        pollService.closePoll(pollId, user);
        redirectAttributes.addFlashAttribute("successMessage", "Poll closed.");
        return "redirect:/web/polls/" + pollId;
    }

    @GetMapping("/event/{eventId:[0-9]+}")
    public String eventPolls(@PathVariable Long eventId, Model model) {
        model.addAttribute("polls", pollService.getEventPolls(eventId));
        model.addAttribute("eventId", eventId);
        model.addAttribute("activeNav", "events");
        return "polls/event";
    }
}
