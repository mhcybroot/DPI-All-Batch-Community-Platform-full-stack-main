package mh.cyb.root.DpiBatchMeetBackend.modules.web;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.service.BirthdayService;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.service.ForumService;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.service.NoticeService;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.service.EventService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web")
public class DashboardViewController {

    private final EventService eventService;
    private final BirthdayService birthdayService;
    private final NoticeService noticeService;
    private final ForumService forumService;

    public DashboardViewController(EventService eventService, BirthdayService birthdayService,
            NoticeService noticeService, ForumService forumService) {
        this.eventService = eventService;
        this.birthdayService = birthdayService;
        this.noticeService = noticeService;
        this.forumService = forumService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activeNav", "dashboard");
        try {
            model.addAttribute("upcomingEvents", eventService.getUpcomingEvents());
        } catch (Exception e) {
            model.addAttribute("upcomingEvents", java.util.List.of());
        }
        try {
            model.addAttribute("todayBirthdays", birthdayService.getTodayBirthdays());
        } catch (Exception e) {
            model.addAttribute("todayBirthdays", java.util.List.of());
        }
        try {
            model.addAttribute("notices", noticeService.getAllActiveNotices(PageRequest.of(0, 5)).getContent());
        } catch (Exception e) {
            model.addAttribute("notices", java.util.List.of());
        }
        try {
            model.addAttribute("recentForumPosts", forumService.getRecentActivity(5));
        } catch (Exception e) {
            model.addAttribute("recentForumPosts", java.util.List.of());
        }

        return "dashboard";
    }

    @GetMapping("")
    public String redirectToDashboard() {
        return "redirect:/web/dashboard";
    }
}
