package mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.domain.VoteType;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface KnowledgeService {
    QuestionDto createQuestion(CreateQuestionRequest request, User user);

    Page<QuestionDto> getAllQuestions(String sort, Pageable pageable);

    QuestionDto getQuestionById(Long id);

    AnswerDto addAnswer(Long questionId, CreateAnswerRequest request, User user);

    List<AnswerDto> getAnswers(Long questionId);

    void voteQuestion(Long questionId, VoteType voteType, User user);

    void voteAnswer(Long answerId, VoteType voteType, User user);

    AnswerDto acceptAnswer(Long answerId, User user);
}
