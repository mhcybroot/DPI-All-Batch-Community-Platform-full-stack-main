package mh.cyb.root.DpiBatchMeetBackend.modules.web.community;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.CreateNoticeRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.service.NoticeService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/notices")
public class NoticeViewController {

    private final NoticeService noticeService;
    private final UserService userService;

    public NoticeViewController(NoticeService noticeService, UserService userService) {
        this.noticeService = noticeService;
        this.userService = userService;
    }

    @GetMapping("")
    public String listNotices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        model.addAttribute("noticesPage",
                noticeService.getAllActiveNotices(org.springframework.data.domain.PageRequest.of(page, size)));
        model.addAttribute("activeNav", "notices");
        return "notices/index";
    }

    @GetMapping("/new")
    public String newNoticeForm(Model model) {
        model.addAttribute("activeNav", "notices");
        return "notices/new";
    }

    @PostMapping("")
    public String createNotice(@ModelAttribute CreateNoticeRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        noticeService.createNotice(request, user.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Notice published!");
        return "redirect:/web/notices";
    }

    @PostMapping("/{id:[0-9]+}/delete")
    public String deleteNotice(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        noticeService.deleteNotice(id);
        redirectAttributes.addFlashAttribute("successMessage", "Notice deleted.");
        return "redirect:/web/notices";
    }

    // --- Phase G: Edit Notice ---

    @GetMapping("/{id:[0-9]+}/edit")
    public String editNoticeForm(@PathVariable Long id, Model model) {
        model.addAttribute("notice", noticeService.getAllActiveNotices().stream()
                .filter(n -> n.getId().equals(id)).findFirst().orElseThrow());
        model.addAttribute("activeNav", "notices");
        return "notices/edit";
    }

    @PostMapping("/{id:[0-9]+}/edit")
    public String updateNotice(@PathVariable Long id, @ModelAttribute CreateNoticeRequest request,
            RedirectAttributes redirectAttributes) {
        noticeService.updateNotice(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Notice updated!");
        return "redirect:/web/notices";
    }
}
