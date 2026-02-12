package mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.domain.VoteType;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.service.KnowledgeService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class KnowledgeControllerTest {

    @Mock
    private KnowledgeService knowledgeService;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private KnowledgeController knowledgeController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    }

    @Test
    void askQuestion_ShouldReturnCreated() {
        CreateQuestionRequest request = new CreateQuestionRequest();
        QuestionDto dto = QuestionDto.builder().id(1L).build();
        when(knowledgeService.createQuestion(any(), any())).thenReturn(dto);

        ResponseEntity<QuestionDto> response = knowledgeController.askQuestion(userDetails, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getQuestion_ShouldReturnOk() {
        QuestionDto dto = QuestionDto.builder().id(1L).build();
        when(knowledgeService.getQuestionById(1L)).thenReturn(dto);

        ResponseEntity<QuestionDto> response = knowledgeController.getQuestion(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getAnswers_ShouldReturnOk() {
        when(knowledgeService.getAnswers(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<AnswerDto>> response = knowledgeController.getAnswers(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getQuestions_ShouldReturnOk() {
        when(knowledgeService.getAllQuestions(any(), any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        ResponseEntity<Page<QuestionDto>> response = knowledgeController.getQuestions("newest", 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void postAnswer_ShouldReturnOk() {
        CreateAnswerRequest request = new CreateAnswerRequest();
        AnswerDto dto = AnswerDto.builder().id(1L).build();
        when(knowledgeService.addAnswer(anyLong(), any(), any())).thenReturn(dto);

        ResponseEntity<AnswerDto> response = knowledgeController.postAnswer(1L, userDetails, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void voteQuestion_ShouldReturnOk() {
        VoteRequest request = new VoteRequest();
        request.setVoteType(VoteType.UP);

        ResponseEntity<Void> response = knowledgeController.voteQuestion(1L, userDetails, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void acceptAnswer_ShouldReturnOk() {
        AnswerDto dto = AnswerDto.builder().isAccepted(true).build();
        when(knowledgeService.acceptAnswer(anyLong(), any())).thenReturn(dto);

        ResponseEntity<AnswerDto> response = knowledgeController.acceptAnswer(1L, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void voteAnswer_ShouldReturnOk() {
        VoteRequest request = new VoteRequest();
        request.setVoteType(VoteType.UP);

        ResponseEntity<Void> response = knowledgeController.voteAnswer(1L, userDetails, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
