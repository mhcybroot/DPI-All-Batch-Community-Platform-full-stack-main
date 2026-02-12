package mh.cyb.root.DpiBatchMeetBackend.modules.web.professional;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateBusinessRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateBusinessRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.service.BusinessService;
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
@RequestMapping("/web/business")
public class BusinessViewController {

    private final BusinessService businessService;
    private final UserService userService;

    public BusinessViewController(BusinessService businessService, UserService userService) {
        this.businessService = businessService;
        this.userService = userService;
    }

    @GetMapping("")
    public String listBusinesses(@RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("businessPage", businessService.getAllBusinesses(search, PageRequest.of(page, 12)));
        model.addAttribute("search", search);
        model.addAttribute("activeNav", "directory");
        return "business/index";
    }

    @GetMapping("/my-business")
    public String myBusiness(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("businessPage", businessService.getBusinessesByUser(user, PageRequest.of(page, 12)));
        model.addAttribute("activeNav", "directory");
        return "business/my-business";
    }

    @GetMapping("/{id:[0-9]+}")
    public String businessDetail(@PathVariable Long id, Model model) {
        model.addAttribute("business", businessService.getBusinessById(id));
        model.addAttribute("activeNav", "directory");
        return "business/detail";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("activeNav", "directory");
        return "business/register";
    }

    @PostMapping("")
    public String registerBusiness(@ModelAttribute CreateBusinessRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        businessService.registerBusiness(request, user);
        redirectAttributes.addFlashAttribute("successMessage", "Business registered!");
        return "redirect:/web/business";
    }

    // --- Phase E: Edit + Delete Business ---

    @GetMapping("/{id:[0-9]+}/edit")
    public String editBusinessForm(@PathVariable Long id, Model model) {
        model.addAttribute("business", businessService.getBusinessById(id));
        model.addAttribute("activeNav", "directory");
        return "business/edit";
    }

    @PostMapping("/{id:[0-9]+}/edit")
    public String updateBusiness(@PathVariable Long id, @ModelAttribute UpdateBusinessRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        businessService.updateBusiness(id, request, user);
        redirectAttributes.addFlashAttribute("successMessage", "Business updated!");
        return "redirect:/web/business/" + id;
    }

    @PostMapping("/{id:[0-9]+}/delete")
    public String deleteBusiness(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        businessService.deleteBusiness(id, user);
        redirectAttributes.addFlashAttribute("successMessage", "Business deleted.");
        return "redirect:/web/business";
    }
}
