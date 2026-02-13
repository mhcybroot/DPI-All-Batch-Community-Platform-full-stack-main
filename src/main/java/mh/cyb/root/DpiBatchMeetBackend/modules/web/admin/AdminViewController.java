package mh.cyb.root.DpiBatchMeetBackend.modules.web.admin;

import mh.cyb.root.DpiBatchMeetBackend.modules.admin.service.ApprovalService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.CreateUserRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
@RequestMapping("/web/admin")
public class AdminViewController {

    private final ApprovalService approvalService;
    private final UserService userService;
    private final mh.cyb.root.DpiBatchMeetBackend.modules.admin.service.AuditService auditService;

    public AdminViewController(ApprovalService approvalService, UserService userService,
            mh.cyb.root.DpiBatchMeetBackend.modules.admin.service.AuditService auditService) {
        this.approvalService = approvalService;
        this.userService = userService;
        this.auditService = auditService;
    }

    @GetMapping("/approvals")
    public String pendingApprovals(Model model) {
        model.addAttribute("requests", approvalService.getPendingRequests());
        model.addAttribute("activeNav", "admin");
        return "admin/approvals";
    }

    @PostMapping("/approvals/{id}/approve")
    public String approve(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        approvalService.approveRequest(id, user);
        redirectAttributes.addFlashAttribute("successMessage", "User approved!");
        return "redirect:/web/admin/approvals";
    }

    @PostMapping("/approvals/{id}/reject")
    public String reject(@PathVariable Long id,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        approvalService.rejectRequest(id, user, reason);
        redirectAttributes.addFlashAttribute("successMessage", "User rejected.");
        return "redirect:/web/admin/approvals";
    }

    @GetMapping("/users/create")
    public String createUserForm(Model model) {
        model.addAttribute("roles", Role.values());
        model.addAttribute("activeNav", "admin");
        return "admin/create-user";
    }

    @PostMapping("/users/create")
    public String createUser(@RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) Set<String> roles,
            RedirectAttributes redirectAttributes) {
        CreateUserRequest request = new CreateUserRequest();
        request.setFullName(fullName);
        request.setEmail(email);
        request.setPassword(password);
        request.setRoles(roles != null ? roles : Set.of("MEMBER"));
        userService.createUser(request);
        redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
        return "redirect:/web/admin/approvals";
    }

    @GetMapping("/users/{id}")
    public String viewUserDetails(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserDtoById(id));
        model.addAttribute("auditLogs", auditService.getLogsByActor(id));
        model.addAttribute("activeNav", "admin");
        return "admin/user-details";
    }

    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("activeNav", "admin");
        return "admin/users";
    }
}
