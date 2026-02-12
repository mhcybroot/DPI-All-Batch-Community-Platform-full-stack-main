package mh.cyb.root.DpiBatchMeetBackend.modules.web.profile;

import mh.cyb.root.DpiBatchMeetBackend.modules.profile.service.DirectoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/web/directory")
public class DirectoryViewController {

    private final DirectoryService directoryService;

    public DirectoryViewController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @GetMapping("")
    public String directoryPage(Model model) {
        model.addAttribute("activeNav", "directory");
        return "directory/search";
    }

    @GetMapping("/search")
    public String searchDirectory(@RequestParam(defaultValue = "") String query, Model model) {
        if (!query.isBlank()) {
            model.addAttribute("results", directoryService.searchProfiles(query));
        }
        model.addAttribute("query", query);
        model.addAttribute("activeNav", "directory");

        // If HTMX request, return only the fragment
        return "directory/search";
    }

    @GetMapping("/filter")
    public String filterDirectory(@RequestParam(required = false) String skill,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String employmentStatus,
            Model model) {
        if ((skill != null && !skill.isBlank()) ||
                (location != null && !location.isBlank()) ||
                (employmentStatus != null && !employmentStatus.isBlank())) {
            model.addAttribute("results", directoryService.filterProfiles(
                    skill != null && !skill.isBlank() ? skill : null,
                    location != null && !location.isBlank() ? location : null,
                    employmentStatus != null && !employmentStatus.isBlank() ? employmentStatus : null));
        }
        model.addAttribute("filterSkill", skill);
        model.addAttribute("filterLocation", location);
        model.addAttribute("filterEmploymentStatus", employmentStatus);
        model.addAttribute("activeNav", "directory");
        return "directory/search";
    }
}
