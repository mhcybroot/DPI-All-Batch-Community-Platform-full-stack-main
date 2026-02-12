package mh.cyb.root.DpiBatchMeetBackend.modules.web.community;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.service.BirthdayService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/birthdays")
public class BirthdayViewController {

    private final BirthdayService birthdayService;

    public BirthdayViewController(BirthdayService birthdayService) {
        this.birthdayService = birthdayService;
    }

    @GetMapping("")
    public String birthdayPage(Model model) {
        model.addAttribute("todayBirthdays", birthdayService.getTodayBirthdays());
        model.addAttribute("upcomingBirthdays", birthdayService.getUpcomingBirthdays(7));
        model.addAttribute("activeNav", "dashboard");
        return "birthdays/index";
    }

}
