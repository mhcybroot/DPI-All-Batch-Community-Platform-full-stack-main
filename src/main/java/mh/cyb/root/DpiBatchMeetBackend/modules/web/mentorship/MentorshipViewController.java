package mh.cyb.root.DpiBatchMeetBackend.modules.web.mentorship;

import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.domain.ConnectionStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.dto.CreateConnectionRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.dto.RegisterMentorRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.service.MentorshipService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/mentorship")
public class MentorshipViewController {

    private final MentorshipService mentorshipService;
    private final UserService userService;

    public MentorshipViewController(MentorshipService mentorshipService, UserService userService) {
        this.mentorshipService = mentorshipService;
        this.userService = userService;
    }

    @GetMapping("")
    public String mentorshipHome(@RequestParam(defaultValue = "") String expertise,
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        if (userDetails != null) {
            User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
            model.addAttribute("currentUserId", user.getId());
        }
        model.addAttribute("mentorsPage", mentorshipService.searchMentors(expertise, PageRequest.of(page, 12)));
        model.addAttribute("expertise", expertise);
        model.addAttribute("activeNav", "mentorship");
        return "mentorship/index";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("activeNav", "mentorship");
        return "mentorship/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterMentorRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        mentorshipService.registerAsMentor(request, user);
        redirectAttributes.addFlashAttribute("successMessage", "Registered as mentor!");
        return "redirect:/web/mentorship";
    }

    @PostMapping("/connect")
    public String sendRequest(@ModelAttribute CreateConnectionRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        mentorshipService.sendRequest(request, user);
        redirectAttributes.addFlashAttribute("successMessage", "Mentorship request sent!");
        return "redirect:/web/mentorship";
    }

    @GetMapping("/requests")
    public String myRequests(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("myRequests", mentorshipService.getMyRequests(user));
        model.addAttribute("incomingRequests", mentorshipService.getIncomingRequests(user));
        model.addAttribute("activeNav", "mentorship");
        return "mentorship/requests";
    }

    @PostMapping("/requests/{id}/update")
    public String updateStatus(@PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        mentorshipService.updateRequestStatus(id, ConnectionStatus.valueOf(status), user);
        redirectAttributes.addFlashAttribute("successMessage", "Request " + status.toLowerCase() + "!");
        return "redirect:/web/mentorship/requests";
    }
}
