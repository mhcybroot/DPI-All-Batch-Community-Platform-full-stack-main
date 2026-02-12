package mh.cyb.root.DpiBatchMeetBackend.modules.event.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    List<Poll> findByEventId(Long eventId);

    List<Poll> findByClosedFalse();
}
