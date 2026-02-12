package mh.cyb.root.DpiBatchMeetBackend.modules.event.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.Event;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatusOrderByEventDateAsc(EventStatus status);

    List<Event> findByOrganizerId(Long organizerId);

    List<Event> findByEventDateAfterOrderByEventDateAsc(LocalDateTime date);
}
