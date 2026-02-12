package mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.domain.MentorProfile;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.domain.MentorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {
    Optional<MentorProfile> findByUser_Id(Long userId);

    // Basic search functionality - can be expanded with @Query for skills
    Page<MentorProfile> findByStatus(MentorStatus status, Pageable pageable);

    // For expertise search
    Page<MentorProfile> findByExpertiseContainingIgnoreCaseAndStatus(String expertise, MentorStatus status,
            Pageable pageable);
}
