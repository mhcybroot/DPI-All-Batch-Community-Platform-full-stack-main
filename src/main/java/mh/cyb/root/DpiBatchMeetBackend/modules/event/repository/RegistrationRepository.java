package mh.cyb.root.DpiBatchMeetBackend.modules.event.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.Registration;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByEventId(Long eventId);

    List<Registration> findByEventIdAndStatus(Long eventId, RegistrationStatus status);

    List<Registration> findByUserId(Long userId);

    Optional<Registration> findByEventIdAndUserId(Long eventId, Long userId);

    int countByEventIdAndStatus(Long eventId, RegistrationStatus status);

    // For auto-promotion: Find oldest waitlisted
    Optional<Registration> findFirstByEventIdAndStatusOrderByRegisteredAtAsc(Long eventId, RegistrationStatus status);
}
