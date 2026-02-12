package mh.cyb.root.DpiBatchMeetBackend.modules.event.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollOptionRepository extends JpaRepository<PollOption, Long> {
    List<PollOption> findByPollId(Long pollId);
}
