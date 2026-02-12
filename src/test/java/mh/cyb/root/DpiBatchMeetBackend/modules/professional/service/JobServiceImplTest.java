package mh.cyb.root.DpiBatchMeetBackend.modules.professional.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobPost;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateJobRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.JobPostDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateJobRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.mapper.JobMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.repository.JobPostRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JobServiceImplTest {

    @Mock
    private JobPostRepository jobPostRepository;

    @Mock
    private JobMapper jobMapper;

    @InjectMocks
    private JobServiceImpl jobService;

    private User user;
    private JobPost jobPost;
    private JobPostDto jobPostDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        jobPost = new JobPost();
        jobPost.setId(1L);
        jobPost.setPostedBy(user);
        jobPost.setStatus(JobStatus.ACTIVE);

        jobPostDto = new JobPostDto();
        jobPostDto.setId(1L);
    }

    @Test
    void createJob_ShouldReturnDto() {
        CreateJobRequest request = new CreateJobRequest();
        when(jobMapper.toEntity(request)).thenReturn(jobPost);
        when(jobPostRepository.save(any(JobPost.class))).thenReturn(jobPost);
        when(jobMapper.toDto(jobPost)).thenReturn(jobPostDto);

        JobPostDto result = jobService.createJob(request, user);

        assertNotNull(result);
        verify(jobPostRepository).save(any(JobPost.class));
    }

    @Test
    void getAllJobs_ShouldReturnPage() {
        Page<JobPost> page = new PageImpl<>(Collections.singletonList(jobPost));
        when(jobPostRepository.findByStatus(any(), any())).thenReturn(page);
        when(jobMapper.toDto(any())).thenReturn(jobPostDto);

        Page<JobPostDto> result = jobService.getAllJobs(JobStatus.ACTIVE, null, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getJobById_ShouldReturnDto() {
        when(jobPostRepository.findById(1L)).thenReturn(Optional.of(jobPost));
        when(jobMapper.toDto(jobPost)).thenReturn(jobPostDto);

        JobPostDto result = jobService.getJobById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void updateJob_ShouldReturnDto() {
        UpdateJobRequest request = new UpdateJobRequest();
        when(jobPostRepository.findById(1L)).thenReturn(Optional.of(jobPost));
        doNothing().when(jobMapper).updateJobFromRequest(request, jobPost);
        when(jobPostRepository.save(jobPost)).thenReturn(jobPost);
        when(jobMapper.toDto(jobPost)).thenReturn(jobPostDto);

        JobPostDto result = jobService.updateJob(1L, request, user);

        assertNotNull(result);
        verify(jobPostRepository).save(jobPost);
    }

    @Test
    void updateJob_ShouldThrowException_WhenUserIsNotOwner() {
        User otherUser = new User();
        otherUser.setId(2L);
        when(jobPostRepository.findById(1L)).thenReturn(Optional.of(jobPost));

        assertThrows(RuntimeException.class, () -> jobService.updateJob(1L, new UpdateJobRequest(), otherUser));
    }

    @Test
    void deleteJob_ShouldDelete_WhenUserIsOwner() {
        when(jobPostRepository.findById(1L)).thenReturn(Optional.of(jobPost));

        jobService.deleteJob(1L, user);

        verify(jobPostRepository).delete(jobPost);
    }

    @Test
    void changeStatus_ShouldUpdateStatus() {
        when(jobPostRepository.findById(1L)).thenReturn(Optional.of(jobPost));
        when(jobPostRepository.save(jobPost)).thenReturn(jobPost);
        when(jobMapper.toDto(jobPost)).thenReturn(jobPostDto);

        JobPostDto result = jobService.changeStatus(1L, JobStatus.CLOSED, user);

        assertNotNull(result);
        verify(jobPostRepository).save(jobPost);
    }
}
