package mh.cyb.root.DpiBatchMeetBackend.modules.community.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.domain.Memory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoryRepository extends JpaRepository<Memory, Long> {
    List<Memory> findAllByOrderByCreatedAtDesc();
}
