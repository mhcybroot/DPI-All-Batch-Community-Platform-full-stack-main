package mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.domain.KnowledgeVote;
import mh.cyb.root.DpiBatchMeetBackend.modules.knowledge.domain.VoteTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KnowledgeVoteRepository extends JpaRepository<KnowledgeVote, Long> {
    Optional<KnowledgeVote> findByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, VoteTargetType targetType);
}
