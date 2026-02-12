package mh.cyb.root.DpiBatchMeetBackend.modules.web.auth;

import mh.cyb.root.DpiBatchMeetBackend.modules.auth.dto.RegisterRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/web/auth")
public class AuthViewController {

    private final UserService userService;

    public AuthViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {
        try {
            if (userService.existsByEmail(email)) {
                model.addAttribute("error", "An account with this email already exists.");
                return "auth/register";
            }

            RegisterRequest request = new RegisterRequest();
            request.setFullName(fullName);
            request.setEmail(email);
            request.setPassword(password);
            userService.registerUser(request);

            return "redirect:/web/auth/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }
}
