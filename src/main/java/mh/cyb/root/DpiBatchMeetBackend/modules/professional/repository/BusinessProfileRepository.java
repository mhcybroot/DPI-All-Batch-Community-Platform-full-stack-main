package mh.cyb.root.DpiBatchMeetBackend.modules.professional.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BusinessProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, Long> {
    List<BusinessProfile> findByOwner_Id(Long userId);

    org.springframework.data.domain.Page<BusinessProfile> findByOwner(
            mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User owner,
            org.springframework.data.domain.Pageable pageable);
}
