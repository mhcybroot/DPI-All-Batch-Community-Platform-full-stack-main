package mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobType;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserDto;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostDto {
    private Long id;
    private String title;
    private String companyName;
    private String location;
    private JobType jobType;
    private String description;
    private String requirements;
    private String applicationLink;
    private LocalDate deadline;
    private UserDto postedBy;
    private JobStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
