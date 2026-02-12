package mh.cyb.root.DpiBatchMeetBackend.modules.web.professional;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobType;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateJobRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateJobRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.service.JobService;
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
@RequestMapping("/web/jobs")
public class JobViewController {

    private final JobService jobService;
    private final UserService userService;

    public JobViewController(JobService jobService, UserService userService) {
        this.jobService = jobService;
        this.userService = userService;
    }

    @GetMapping("")
    public String listJobs(@RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) JobType jobType,
            Model model) {
        if (status == null) {
            status = JobStatus.ACTIVE;
        }
        model.addAttribute("jobsPage", jobService.getAllJobs(status, jobType, PageRequest.of(page, 10)));
        model.addAttribute("paramStatus", status);
        model.addAttribute("paramJobType", jobType);
        model.addAttribute("jobStatuses", JobStatus.values());
        model.addAttribute("jobTypes", JobType.values());
        model.addAttribute("activeNav", "jobs");
        return "jobs/index";
    }

    @GetMapping("/my-jobs")
    public String myJobs(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("jobsPage", jobService.getJobsByUser(user, PageRequest.of(page, 10)));
        model.addAttribute("activeNav", "jobs");
        return "jobs/my-jobs";
    }

    @GetMapping("/{id:[0-9]+}")
    public String jobDetail(@PathVariable Long id, Model model) {
        model.addAttribute("job", jobService.getJobById(id));
        model.addAttribute("activeNav", "jobs");
        return "jobs/detail";
    }

    @GetMapping("/new")
    public String newJobForm(Model model) {
        model.addAttribute("activeNav", "jobs");
        return "jobs/new";
    }

    @PostMapping("")
    public String createJob(@ModelAttribute CreateJobRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        jobService.createJob(request, user);
        redirectAttributes.addFlashAttribute("successMessage", "Job posted!");
        return "redirect:/web/jobs";
    }

    @PostMapping("/{id:[0-9]+}/delete")
    public String deleteJob(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        jobService.deleteJob(id, user);
        redirectAttributes.addFlashAttribute("successMessage", "Job deleted.");
        return "redirect:/web/jobs";
    }

    // --- Phase E: Edit Job + Status ---

    @GetMapping("/{id:[0-9]+}/edit")
    public String editJobForm(@PathVariable Long id, Model model) {
        model.addAttribute("job", jobService.getJobById(id));
        model.addAttribute("jobTypes", JobType.values());
        model.addAttribute("activeNav", "jobs");
        return "jobs/edit";
    }

    @PostMapping("/{id:[0-9]+}/edit")
    public String updateJob(@PathVariable Long id, @ModelAttribute UpdateJobRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        jobService.updateJob(id, request, user);
        redirectAttributes.addFlashAttribute("successMessage", "Job updated!");
        return "redirect:/web/jobs/" + id;
    }

    @PostMapping("/{id:[0-9]+}/status")
    public String changeStatus(@PathVariable Long id, @RequestParam JobStatus status,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        jobService.changeStatus(id, status, user);
        redirectAttributes.addFlashAttribute("successMessage", "Job status updated.");
        return "redirect:/web/jobs/" + id;
    }
}
