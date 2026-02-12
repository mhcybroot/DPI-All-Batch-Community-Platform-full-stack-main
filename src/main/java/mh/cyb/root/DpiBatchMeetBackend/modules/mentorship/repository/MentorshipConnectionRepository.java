package mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.domain.ConnectionStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.domain.MentorshipConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentorshipConnectionRepository extends JpaRepository<MentorshipConnection, Long> {
    List<MentorshipConnection> findByMentor_IdAndStatus(Long mentorId, ConnectionStatus status);

    List<MentorshipConnection> findByMentee_Id(Long menteeId);

    Optional<MentorshipConnection> findByMentor_IdAndMentee_IdAndStatusNot(Long mentorId, Long menteeId,
            ConnectionStatus status);
}
