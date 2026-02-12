package mh.cyb.root.DpiBatchMeetBackend.modules.event.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Query("SELECT v FROM Vote v JOIN v.pollOption po WHERE po.poll.id = :pollId AND v.voterId = :voterId")
    Optional<Vote> findByPollIdAndVoterId(Long pollId, Long voterId);

    @Query("SELECT v FROM Vote v JOIN v.pollOption po WHERE po.poll.id = :pollId")
    List<Vote> findByPollId(Long pollId);

    int countByPollOptionId(Long pollOptionId);
}
