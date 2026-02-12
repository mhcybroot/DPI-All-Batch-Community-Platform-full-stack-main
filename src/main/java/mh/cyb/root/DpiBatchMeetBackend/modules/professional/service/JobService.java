package mh.cyb.root.DpiBatchMeetBackend.modules.professional.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobType;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateJobRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.JobPostDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateJobRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobService {
    JobPostDto createJob(CreateJobRequest request, User user);

    Page<JobPostDto> getAllJobs(JobStatus status, JobType jobType, Pageable pageable);

    JobPostDto getJobById(Long id);

    JobPostDto updateJob(Long id, UpdateJobRequest request, User user);

    void deleteJob(Long id, User user); // Soft delete or Hard delete? Spec says "Manage own posts
                                        // (Update/Delete/Close)"

    JobPostDto changeStatus(Long id, JobStatus status, User user);

    Page<JobPostDto> getJobsByUser(User user, Pageable pageable);
}
