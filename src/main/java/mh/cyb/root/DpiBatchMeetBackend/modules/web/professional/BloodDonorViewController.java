package mh.cyb.root.DpiBatchMeetBackend.modules.web.professional;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BloodGroup;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateBloodDonorRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.service.BloodDonorService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/web/blood-donors")
public class BloodDonorViewController {

    private final BloodDonorService bloodDonorService;
    private final UserService userService;

    public BloodDonorViewController(BloodDonorService bloodDonorService, UserService userService) {
        this.bloodDonorService = bloodDonorService;
        this.userService = userService;
    }

    @GetMapping("")
    public String searchDonors(@RequestParam(required = false) BloodGroup bloodGroup,
            @RequestParam(required = false) String location,
            Model model) {
        if (bloodGroup != null || (location != null && !location.isBlank())) {
            model.addAttribute("donors", bloodDonorService.searchDonors(bloodGroup, location));
        }
        model.addAttribute("bloodGroups", BloodGroup.values());
        model.addAttribute("activeNav", "directory");
        return "blood-donors/index";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("bloodGroups", BloodGroup.values());
        model.addAttribute("activeNav", "directory");
        return "blood-donors/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute CreateBloodDonorRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        bloodDonorService.registerData(request, user);
        redirectAttributes.addFlashAttribute("successMessage", "Blood donor profile registered!");
        return "redirect:/web/blood-donors";
    }

    @PostMapping("/availability")
    public String toggleAvailability(@RequestParam boolean isAvailable,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        bloodDonorService.updateAvailability(isAvailable, user);
        redirectAttributes.addFlashAttribute("successMessage", "Availability updated!");
        return "redirect:/web/blood-donors/my-profile";
    }

    // --- Phase F: My Profile + Last Donation ---

    @GetMapping("/my-profile")
    public String myProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("profile", bloodDonorService.getMyProfile(user));
        model.addAttribute("activeNav", "directory");
        return "blood-donors/my-profile";
    }

    @PostMapping("/last-donation")
    public String updateLastDonation(@RequestParam String lastDonationDate,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        bloodDonorService.updateLastDonationDate(LocalDate.parse(lastDonationDate), user);
        redirectAttributes.addFlashAttribute("successMessage", "Last donation date updated!");
        return "redirect:/web/blood-donors/my-profile";
    }
}
