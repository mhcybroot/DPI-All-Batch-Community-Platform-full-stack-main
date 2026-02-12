package mh.cyb.root.DpiBatchMeetBackend.modules.professional.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateJobRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.JobPostDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateJobRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.service.JobService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class JobControllerTest {

    @Mock
    private JobService jobService;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JobController jobController;

    private JobPostDto jobPostDto;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jobPostDto = new JobPostDto();
        jobPostDto.setId(1L);
        jobPostDto.setTitle("Software Engineer");

        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
    }

    @Test
    void createJob_ShouldReturnCreated() {
        CreateJobRequest request = new CreateJobRequest();
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(jobService.createJob(any(), any())).thenReturn(jobPostDto);

        ResponseEntity<JobPostDto> response = jobController.createJob(userDetails, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(jobPostDto, response.getBody());
    }

    @Test
    void getAllJobs_ShouldReturnOk() {
        Page<JobPostDto> page = new PageImpl<>(Collections.singletonList(jobPostDto));
        when(jobService.getAllJobs(any(), any(), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<JobPostDto>> response = jobController.getAllJobs(JobStatus.ACTIVE, null,
                Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getJobById_ShouldReturnOk() {
        when(jobService.getJobById(1L)).thenReturn(jobPostDto);

        ResponseEntity<JobPostDto> response = jobController.getJobById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jobPostDto, response.getBody());
    }

    @Test
    void updateJob_ShouldReturnOk() {
        UpdateJobRequest request = new UpdateJobRequest();
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(jobService.updateJob(eq(1L), any(), any())).thenReturn(jobPostDto);

        ResponseEntity<JobPostDto> response = jobController.updateJob(userDetails, 1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteJob_ShouldReturnNoContent() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        doNothing().when(jobService).deleteJob(eq(1L), any());

        ResponseEntity<Void> response = jobController.deleteJob(userDetails, 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void changeStatus_ShouldReturnOk() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userService.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(jobService.changeStatus(eq(1L), any(JobStatus.class), any())).thenReturn(jobPostDto);

        ResponseEntity<JobPostDto> response = jobController.changeStatus(userDetails, 1L, JobStatus.CLOSED);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
