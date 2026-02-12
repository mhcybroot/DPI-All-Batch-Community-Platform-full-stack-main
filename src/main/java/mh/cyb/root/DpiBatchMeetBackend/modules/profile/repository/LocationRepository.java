package mh.cyb.root.DpiBatchMeetBackend.modules.profile.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}
