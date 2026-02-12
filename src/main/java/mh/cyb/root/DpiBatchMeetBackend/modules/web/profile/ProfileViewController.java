package mh.cyb.root.DpiBatchMeetBackend.modules.web.profile;

import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.PrivacySetting;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileUpdateRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.repository.PrivacySettingRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.service.ProfileService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/profile")
public class ProfileViewController {

    private final ProfileService profileService;
    private final UserService userService;
    private final PrivacySettingRepository privacySettingRepository;

    public ProfileViewController(ProfileService profileService, UserService userService,
            PrivacySettingRepository privacySettingRepository) {
        this.profileService = profileService;
        this.userService = userService;
        this.privacySettingRepository = privacySettingRepository;
    }

    @GetMapping("")
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("profile", profileService.getProfile(user));
        model.addAttribute("activeNav", "profile");
        return "profile/view";
    }

    @GetMapping("/edit")
    public String editProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("profile", profileService.getProfile(user));
        model.addAttribute("activeNav", "profile");
        return "profile/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute ProfileUpdateRequest request,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        profileService.updateProfile(user, request);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/web/profile";
    }

    @PostMapping("/skills")
    public String addSkills(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String skills,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Set<String> skillSet = Arrays.stream(skills.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        profileService.addSkills(user, skillSet);
        redirectAttributes.addFlashAttribute("successMessage", "Skills added successfully!");
        return "redirect:/web/profile";
    }

    // --- Privacy Settings ---

    @GetMapping("/privacy")
    public String privacySettings(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        PrivacySetting privacy = privacySettingRepository.findByUserId(user.getId())
                .orElseGet(() -> PrivacySetting.builder().user(user).build());
        model.addAttribute("privacy", privacy);
        model.addAttribute("activeNav", "profile");
        return "profile/privacy";
    }

    @PostMapping("/privacy")
    public String updatePrivacy(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "false") boolean showEmail,
            @RequestParam(defaultValue = "false") boolean showPhone,
            @RequestParam(defaultValue = "false") boolean showLocation,
            @RequestParam(defaultValue = "false") boolean showEmployment,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        profileService.updatePrivacy(user, showEmail, showPhone, showLocation, showEmployment);
        redirectAttributes.addFlashAttribute("successMessage", "Privacy settings updated!");
        return "redirect:/web/profile";
    }
}
