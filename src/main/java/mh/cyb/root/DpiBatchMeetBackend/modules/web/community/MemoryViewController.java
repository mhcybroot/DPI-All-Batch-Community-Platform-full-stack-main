package mh.cyb.root.DpiBatchMeetBackend.modules.web.community;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.service.MemoryService;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.UploadMemoryRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/memories")
public class MemoryViewController {

    private final MemoryService memoryService;
    private final UserService userService;

    public MemoryViewController(MemoryService memoryService, UserService userService) {
        this.memoryService = memoryService;
        this.userService = userService;
    }

    @GetMapping("")
    public String listMemories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("memoriesPage",
                memoryService.getAllMemories(org.springframework.data.domain.PageRequest.of(page, size)));
        model.addAttribute("currentUserId", user.getId());
        model.addAttribute("isAdmin", user.getRoles().contains(Role.ADMINISTRATOR));
        model.addAttribute("activeNav", "memories");
        return "memories/index";
    }

    @PostMapping("")
    public String uploadMemory(@ModelAttribute UploadMemoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        memoryService.uploadMemory(request, user.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Memory uploaded!");
        return "redirect:/web/memories";
    }

    @PostMapping("/{id}/delete")
    public String deleteMemory(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        boolean isAdmin = user.getRoles().contains(Role.ADMINISTRATOR);
        memoryService.deleteMemory(id, user.getId(), isAdmin);
        redirectAttributes.addFlashAttribute("successMessage", "Memory deleted.");
        return "redirect:/web/memories";
    }
}
