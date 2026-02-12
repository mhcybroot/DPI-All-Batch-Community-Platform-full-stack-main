package mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.domain.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.dto.AnswerDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.dto.CreateAnswerRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.dto.CreateQuestionRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.dto.QuestionDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.repository.AnswerRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.repository.QuestionRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.repository.KnowledgeVoteRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KnowledgeServiceImplTest {

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private KnowledgeVoteRepository voteRepository;

    @InjectMocks
    private KnowledgeServiceImpl knowledgeService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setFullName("Generous Donor");
    }

    @Test
    void createQuestion_ShouldSaveAndReturnDto() {
        CreateQuestionRequest request = new CreateQuestionRequest();
        request.setTitle("Test Question");
        request.setBody("Body");

        Question question = Question.builder()
                .id(1L)
                .title("Test Question")
                .author(user)
                .build();

        when(questionRepository.save(any(Question.class))).thenReturn(question);

        QuestionDto result = knowledgeService.createQuestion(request, user);

        assertNotNull(result);
        assertEquals("Test Question", result.getTitle());
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    void voteQuestion_ShouldSaveVote() {
        Long qId = 1L;
        Question question = new Question();
        question.setId(qId);
        question.setUpvotes(0);

        when(questionRepository.findById(qId)).thenReturn(Optional.of(question));
        when(voteRepository.findByUserIdAndTargetIdAndTargetType(any(), any(), any()))
                .thenReturn(Optional.empty());

        knowledgeService.voteQuestion(qId, VoteType.UP, user);

        verify(voteRepository).save(any(KnowledgeVote.class));
        verify(questionRepository).save(question);
        assertEquals(1, question.getUpvotes());
    }

    @Test
    void getQuestionById_ShouldReturnDto() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(
                Question.builder().id(1L).title("Title").author(user).build()));

        QuestionDto result = knowledgeService.getQuestionById(1L);

        assertNotNull(result);
        assertEquals("Title", result.getTitle());
    }

    @Test
    void addAnswer_ShouldSaveAndReturnDto() {
        CreateAnswerRequest request = new CreateAnswerRequest();
        request.setBody("Answer Body");

        Question question = new Question();
        question.setId(1L);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerRepository.save(any(Answer.class))).thenReturn(
                Answer.builder().id(1L).body("Answer Body").author(user).build());

        AnswerDto result = knowledgeService.addAnswer(1L, request, user);

        assertNotNull(result);
        assertEquals("Answer Body", result.getBody());
    }

    @Test
    void acceptAnswer_ShouldUpdateAnswerAndQuestion() {
        Question question = new Question();
        question.setId(1L);
        question.setAuthor(user); // User is author
        question.setSolved(false);

        Answer answer = new Answer();
        answer.setId(1L);
        answer.setQuestion(question);
        answer.setAuthor(user);
        answer.setAccepted(false);

        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));
        when(answerRepository.findByQuestionIdOrderByIsAcceptedDescUpvotesDesc(1L))
                .thenReturn(Collections.singletonList(answer));
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        knowledgeService.acceptAnswer(1L, user);

        assertTrue(answer.isAccepted());
        assertTrue(question.isSolved());
        verify(answerRepository, times(1)).findByQuestionIdOrderByIsAcceptedDescUpvotesDesc(1L); // Called to unaccept
                                                                                                 // others
    }

    @Test
    void getAllQuestions_ShouldReturnPage() {
        when(questionRepository.findAllByOrderByCreatedAtDesc(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(org.springframework.data.domain.Page.empty());

        var result = knowledgeService.getAllQuestions("newest", org.springframework.data.domain.Pageable.unpaged());

        assertNotNull(result);
    }

    @Test
    void getAnswers_ShouldReturnList() {
        when(answerRepository.findByQuestionIdOrderByIsAcceptedDescUpvotesDesc(1L))
                .thenReturn(Collections.emptyList());

        var result = knowledgeService.getAnswers(1L);

        assertNotNull(result);
    }

    @Test
    void voteAnswer_ShouldSaveVote() {
        Long aId = 1L;
        Answer answer = new Answer();
        answer.setId(aId);
        answer.setUpvotes(0);

        when(answerRepository.findById(aId)).thenReturn(Optional.of(answer));
        when(voteRepository.findByUserIdAndTargetIdAndTargetType(any(), any(), any()))
                .thenReturn(Optional.empty());

        knowledgeService.voteAnswer(aId, VoteType.UP, user);

        verify(voteRepository).save(any(KnowledgeVote.class));
        verify(answerRepository).save(answer);
        assertEquals(1, answer.getUpvotes());
    }
}
