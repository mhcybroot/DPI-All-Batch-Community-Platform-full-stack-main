package mh.cyb.root.DpiBatchMeetBackend.modules.professional.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BloodDonorProfile;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BloodGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodDonorProfileRepository extends JpaRepository<BloodDonorProfile, Long> {
    Optional<BloodDonorProfile> findByUser_Id(Long userId);

    List<BloodDonorProfile> findByBloodGroupAndLocationContainingIgnoreCaseAndIsAvailableTrue(BloodGroup bloodGroup,
            String location);

    List<BloodDonorProfile> findByBloodGroupAndIsAvailableTrue(BloodGroup bloodGroup);
}
