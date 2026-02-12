package mh.cyb.root.DpiBatchMeetBackend.modules.web.admin;

import mh.cyb.root.DpiBatchMeetBackend.modules.admin.service.AuditService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web/admin/audit-logs")
public class AuditViewController {

    private final AuditService auditService;

    public AuditViewController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("")
    public String auditLogsPage(@RequestParam(required = false) Long userId, Model model) {
        if (userId != null) {
            model.addAttribute("logs", auditService.getLogsByActor(userId));
            model.addAttribute("searchUserId", userId);
        }
        model.addAttribute("activeNav", "admin");
        return "admin/audit-logs";
    }
}
