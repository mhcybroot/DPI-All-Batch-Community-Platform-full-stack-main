package mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionIdOrderByIsAcceptedDescUpvotesDesc(Long questionId);
}
