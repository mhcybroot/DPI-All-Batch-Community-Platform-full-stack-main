package mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.domain.VoteType;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.service.KnowledgeService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
@Tag(name = "Knowledge Module", description = "Q&A System (StackOverflow style)")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final UserService userService;

    @PostMapping("/questions")
    @Operation(summary = "Ask a Question")
    public ResponseEntity<QuestionDto> askQuestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateQuestionRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(knowledgeService.createQuestion(request, user));
    }

    @GetMapping("/questions")
    @Operation(summary = "List Questions", description = "Sort options: 'newest', 'votes', 'unsolved'")
    public ResponseEntity<Page<QuestionDto>> getQuestions(
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(knowledgeService.getAllQuestions(sort, pageable));
    }

    @GetMapping("/questions/{id}")
    @Operation(summary = "Get Question Details")
    public ResponseEntity<QuestionDto> getQuestion(@PathVariable Long id) {
        return ResponseEntity.ok(knowledgeService.getQuestionById(id));
    }

    @PostMapping("/questions/{id}/answers")
    @Operation(summary = "Post an Answer")
    public ResponseEntity<AnswerDto> postAnswer(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateAnswerRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(knowledgeService.addAnswer(id, request, user));
    }

    @GetMapping("/questions/{id}/answers")
    @Operation(summary = "Get Answers for a Question")
    public ResponseEntity<List<AnswerDto>> getAnswers(@PathVariable Long id) {
        return ResponseEntity.ok(knowledgeService.getAnswers(id));
    }

    @PostMapping("/questions/{id}/vote")
    @Operation(summary = "Vote on Question")
    public ResponseEntity<Void> voteQuestion(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody VoteRequest request) {
        User user = getUser(userDetails);
        knowledgeService.voteQuestion(id, request.getVoteType(), user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/answers/{id}/vote")
    @Operation(summary = "Vote on Answer")
    public ResponseEntity<Void> voteAnswer(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody VoteRequest request) {
        User user = getUser(userDetails);
        knowledgeService.voteAnswer(id, request.getVoteType(), user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/answers/{id}/accept")
    @Operation(summary = "Accept an Answer (Author only)")
    public ResponseEntity<AnswerDto> acceptAnswer(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(knowledgeService.acceptAnswer(id, user));
    }

    private User getUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
