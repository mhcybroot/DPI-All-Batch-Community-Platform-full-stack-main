package mh.cyb.root.DpiBatchMeetBackend.modules.professional.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobType;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateJobRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.JobPostDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateJobRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.service.JobService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<JobPostDto> createJob(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateJobRequest request) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new ResponseEntity<>(jobService.createJob(request, user), HttpStatus.CREATED);
    }

    @GetMapping("/mine")
    @Operation(summary = "Get my jobs", description = "Retrieves job posts created by the currently authenticated user.")
    public ResponseEntity<Page<JobPostDto>> getMyJobs(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(jobService.getJobsByUser(user, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<JobPostDto>> getAllJobs(
            @RequestParam(required = false, defaultValue = "ACTIVE") JobStatus status,
            @RequestParam(required = false) JobType jobType,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(jobService.getAllJobs(status, jobType, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostDto> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobPostDto> updateJob(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody UpdateJobRequest request) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(jobService.updateJob(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        jobService.deleteJob(id, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<JobPostDto> changeStatus(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam JobStatus status) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(jobService.changeStatus(id, status, user));
    }
}
