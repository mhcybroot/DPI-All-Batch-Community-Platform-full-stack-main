package mh.cyb.root.DpiBatchMeetBackend.modules.community.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.domain.ForumComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumCommentRepository extends JpaRepository<ForumComment, Long> {
    List<ForumComment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
