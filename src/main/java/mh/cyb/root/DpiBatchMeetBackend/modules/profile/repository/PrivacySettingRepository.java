package mh.cyb.root.DpiBatchMeetBackend.modules.profile.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.PrivacySetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivacySettingRepository extends JpaRepository<PrivacySetting, Long> {
    Optional<PrivacySetting> findByUserId(Long userId);
}
