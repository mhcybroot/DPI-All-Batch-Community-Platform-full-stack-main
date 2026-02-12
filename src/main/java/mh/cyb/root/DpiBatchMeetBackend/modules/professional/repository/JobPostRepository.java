package mh.cyb.root.DpiBatchMeetBackend.modules.professional.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobPost;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    Page<JobPost> findByStatus(JobStatus status, Pageable pageable);

    Page<JobPost> findByStatusAndJobType(JobStatus status, JobType jobType, Pageable pageable);

    Page<JobPost> findByJobType(JobType jobType, Pageable pageable);

    List<JobPost> findByPostedBy_Id(Long userId);

    Page<JobPost> findByPostedBy(mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User postedBy, Pageable pageable);
}
