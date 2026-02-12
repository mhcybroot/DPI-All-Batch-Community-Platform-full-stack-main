package mh.cyb.root.DpiBatchMeetBackend.modules.profile.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmploymentStatusRepository extends JpaRepository<EmploymentStatus, Long> {
    Optional<EmploymentStatus> findByStatusName(String statusName);
}
