package mh.cyb.root.DpiBatchMeetBackend.modules.admin.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.admin.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByActorId(Long actorId);
}
