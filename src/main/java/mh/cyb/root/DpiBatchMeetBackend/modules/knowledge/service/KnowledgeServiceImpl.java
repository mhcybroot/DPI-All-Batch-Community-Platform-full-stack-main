package mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.service;

import lombok.RequiredArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.common.exception.ResourceNotFoundException;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.domain.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.repository.AnswerRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.repository.QuestionRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.repository.KnowledgeVoteRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class KnowledgeServiceImpl implements KnowledgeService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final KnowledgeVoteRepository voteRepository;

    @Override
    public QuestionDto createQuestion(CreateQuestionRequest request, User user) {
        Question question = Question.builder()
                .title(request.getTitle())
                .body(request.getBody())
                .author(user)
                .tags(request.getTags())
                .isSolved(false)
                .build();
        return mapToDto(questionRepository.save(question));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionDto> getAllQuestions(String sort, Pageable pageable) {
        // Simple sort handling, could be expanded
        Page<Question> questions;
        if ("unsolved".equalsIgnoreCase(sort)) {
            questions = questionRepository.findByIsSolvedFalse(pageable);
        } else if ("votes".equalsIgnoreCase(sort)) {
            questions = questionRepository.findAllByOrderByUpvotesDesc(pageable);
        } else {
            questions = questionRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        return questions.map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionDto getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        // increment view count
        // Note: In real app, avoid write on GET or use simpler counter
        return mapToDto(question);
    }

    @Override
    public AnswerDto addAnswer(Long questionId, CreateAnswerRequest request, User user) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        Answer answer = Answer.builder()
                .question(question)
                .author(user)
                .body(request.getBody())
                .isAccepted(false)
                .build();

        return mapToDto(answerRepository.save(answer));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerDto> getAnswers(Long questionId) {
        return answerRepository.findByQuestionIdOrderByIsAcceptedDescUpvotesDesc(questionId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public void voteQuestion(Long questionId, VoteType voteType, User user) {
        handleVote(user, questionId, VoteTargetType.QUESTION, voteType);

        Question question = questionRepository.findById(questionId).orElseThrow();
        // Recalculate counts - simplified for this implementation
        // ideally separate count update logic
        if (voteType == VoteType.UP) {
            question.setUpvotes(question.getUpvotes() + 1);
        } else {
            question.setDownvotes(question.getDownvotes() + 1);
        }
        questionRepository.save(question);
    }

    @Override
    public void voteAnswer(Long answerId, VoteType voteType, User user) {
        handleVote(user, answerId, VoteTargetType.ANSWER, voteType);

        Answer answer = answerRepository.findById(answerId).orElseThrow();
        if (voteType == VoteType.UP) {
            answer.setUpvotes(answer.getUpvotes() + 1);
        } else {
            answer.setDownvotes(answer.getDownvotes() + 1);
        }
        answerRepository.save(answer);
    }

    private void handleVote(User user, Long targetId, VoteTargetType targetType, VoteType voteType) {
        Optional<KnowledgeVote> existingVote = voteRepository.findByUserIdAndTargetIdAndTargetType(user.getId(),
                targetId,
                targetType);

        if (existingVote.isPresent()) {
            KnowledgeVote vote = existingVote.get();
            if (vote.getVoteType() == voteType) {
                // Toggle off (remove vote)
                voteRepository.delete(vote);
                // logic to decrement count would be needed here in robust impl
            } else {
                // Change vote
                vote.setVoteType(voteType);
                voteRepository.save(vote);
            }
        } else {
            KnowledgeVote newVote = KnowledgeVote.builder()
                    .user(user)
                    .targetId(targetId)
                    .targetType(targetType)
                    .voteType(voteType)
                    .build();
            voteRepository.save(newVote);
        }
    }

    @Override
    public AnswerDto acceptAnswer(Long answerId, User user) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        if (!answer.getQuestion().getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("Only the question author can accept an answer");
        }

        // Un-accept any other answer for this question
        List<Answer> answers = answerRepository
                .findByQuestionIdOrderByIsAcceptedDescUpvotesDesc(answer.getQuestion().getId());
        answers.forEach(a -> {
            if (a.isAccepted()) {
                a.setAccepted(false);
                answerRepository.save(a);
            }
        });

        answer.setAccepted(true);
        Answer saved = answerRepository.save(answer);

        Question question = answer.getQuestion();
        question.setSolved(true);
        questionRepository.save(question);

        return mapToDto(saved);
    }

    private QuestionDto mapToDto(Question q) {
        return QuestionDto.builder()
                .id(q.getId())
                .title(q.getTitle())
                .body(q.getBody())
                .author(mapUser(q.getAuthor()))
                .tags(q.getTags())
                .viewCount(q.getViewCount())
                .upvotes(q.getUpvotes())
                .downvotes(q.getDownvotes())
                .isSolved(q.isSolved())
                .answerCount(q.getAnswers() != null ? q.getAnswers().size() : 0)
                .createdAt(q.getCreatedAt())
                .build();
    }

    private AnswerDto mapToDto(Answer a) {
        return AnswerDto.builder()
                .id(a.getId())
                .body(a.getBody())
                .author(mapUser(a.getAuthor()))
                .isAccepted(a.isAccepted())
                .upvotes(a.getUpvotes())
                .downvotes(a.getDownvotes())
                .createdAt(a.getCreatedAt())
                .build();
    }

    private UserSummaryDto mapUser(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getFullName())
                // image url if available
                .build();
    }
}
