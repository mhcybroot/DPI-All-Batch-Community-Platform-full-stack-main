package mh.cyb.root.DpiBatchMeetBackend.modules.community.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.domain.ForumCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumCategoryRepository extends JpaRepository<ForumCategory, Long> {
}
