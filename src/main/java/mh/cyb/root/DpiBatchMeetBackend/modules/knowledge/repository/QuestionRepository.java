package mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // For sorting by most upvoted (simplified approach, ideally needs custom query
    // or derived property)
    Page<Question> findAllByOrderByUpvotesDesc(Pageable pageable);

    Page<Question> findByIsSolvedFalse(Pageable pageable);
}
