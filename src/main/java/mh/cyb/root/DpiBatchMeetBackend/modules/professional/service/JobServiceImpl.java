package mh.cyb.root.DpiBatchMeetBackend.modules.professional.service;

import lombok.RequiredArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobPost;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobType;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateJobRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.JobPostDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateJobRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.mapper.JobMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.repository.JobPostRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JobServiceImpl implements JobService {

    private final JobPostRepository jobPostRepository;
    private final JobMapper jobMapper;

    @Override
    public JobPostDto createJob(CreateJobRequest request, User user) {
        JobPost jobPost = jobMapper.toEntity(request);
        jobPost.setPostedBy(user);
        jobPost = jobPostRepository.save(jobPost);
        return jobMapper.toDto(jobPost);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobPostDto> getAllJobs(JobStatus status, JobType jobType, Pageable pageable) {
        Page<JobPost> posts;
        if (status != null && jobType != null) {
            posts = jobPostRepository.findByStatusAndJobType(status, jobType, pageable);
        } else if (status != null) {
            posts = jobPostRepository.findByStatus(status, pageable);
        } else if (jobType != null) {
            posts = jobPostRepository.findByJobType(jobType, pageable);
        } else {
            posts = jobPostRepository.findAll(pageable);
        }
        return posts.map(jobMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostDto getJobById(Long id) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found")); // TODO: Use custom exception
        return jobMapper.toDto(jobPost);
    }

    @Override
    public JobPostDto updateJob(Long id, UpdateJobRequest request, User user) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!jobPost.getPostedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this job post");
        }

        jobMapper.updateJobFromRequest(request, jobPost);
        jobPost = jobPostRepository.save(jobPost);
        return jobMapper.toDto(jobPost);
    }

    @Override
    public void deleteJob(Long id, User user) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!jobPost.getPostedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this job post");
        }

        jobPostRepository.delete(jobPost);
    }

    @Override
    public JobPostDto changeStatus(Long id, JobStatus status, User user) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!jobPost.getPostedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this job post");
        }

        jobPost.setStatus(status);
        jobPost = jobPostRepository.save(jobPost);
        return jobMapper.toDto(jobPost);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobPostDto> getJobsByUser(User user, Pageable pageable) {
        return jobPostRepository.findByPostedBy(user, pageable).map(jobMapper::toDto);
    }
}
