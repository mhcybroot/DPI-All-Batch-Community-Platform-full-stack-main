package mh.cyb.root.DpiBatchMeetBackend.modules.web.knowledge;

import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.domain.VoteType;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.dto.CreateAnswerRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.dto.CreateQuestionRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.service.KnowledgeService;
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
@RequestMapping("/web/knowledge")
public class KnowledgeViewController {

    private final KnowledgeService knowledgeService;
    private final UserService userService;

    public KnowledgeViewController(KnowledgeService knowledgeService, UserService userService) {
        this.knowledgeService = knowledgeService;
        this.userService = userService;
    }

    @GetMapping("")
    public String listQuestions(@RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("questionsPage", knowledgeService.getAllQuestions(sort, PageRequest.of(page, 10)));
        model.addAttribute("sort", sort);
        model.addAttribute("activeNav", "knowledge");
        return "knowledge/index";
    }

    @GetMapping("/{id}")
    public String questionDetail(@PathVariable Long id, Model model) {
        model.addAttribute("question", knowledgeService.getQuestionById(id));
        model.addAttribute("answers", knowledgeService.getAnswers(id));
        model.addAttribute("activeNav", "knowledge");
        return "knowledge/detail";
    }

    @GetMapping("/ask")
    public String askForm(Model model) {
        model.addAttribute("activeNav", "knowledge");
        return "knowledge/ask";
    }

    @PostMapping("/ask")
    public String createQuestion(@ModelAttribute CreateQuestionRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        knowledgeService.createQuestion(request, user);
        redirectAttributes.addFlashAttribute("successMessage", "Question posted!");
        return "redirect:/web/knowledge";
    }

    @PostMapping("/{questionId}/answer")
    public String addAnswer(@PathVariable Long questionId,
            @RequestParam String content,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        CreateAnswerRequest request = new CreateAnswerRequest();
        request.setBody(content);
        knowledgeService.addAnswer(questionId, request, user);
        redirectAttributes.addFlashAttribute("successMessage", "Answer posted!");
        return "redirect:/web/knowledge/" + questionId;
    }

    @PostMapping("/{questionId}/vote")
    public String voteQuestion(@PathVariable Long questionId,
            @RequestParam String voteType,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        knowledgeService.voteQuestion(questionId, VoteType.valueOf(voteType), user);
        return "redirect:/web/knowledge/" + questionId;
    }

    @PostMapping("/answers/{answerId}/vote")
    public String voteAnswer(@PathVariable Long answerId,
            @RequestParam Long questionId,
            @RequestParam String voteType,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        knowledgeService.voteAnswer(answerId, VoteType.valueOf(voteType), user);
        return "redirect:/web/knowledge/" + questionId;
    }

    @PostMapping("/answers/{answerId}/accept")
    public String acceptAnswer(@PathVariable Long answerId,
            @RequestParam Long questionId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        knowledgeService.acceptAnswer(answerId, user);
        redirectAttributes.addFlashAttribute("successMessage", "Answer accepted!");
        return "redirect:/web/knowledge/" + questionId;
    }
}
