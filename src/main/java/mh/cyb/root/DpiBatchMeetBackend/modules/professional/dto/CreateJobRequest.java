package mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.JobType;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateJobRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String location;

    @NotNull(message = "Job type is required")
    private JobType jobType;

    @NotBlank(message = "Description is required")
    private String description;

    private String requirements;

    @NotBlank(message = "Application link is required")
    private String applicationLink;

    @Future(message = "Deadline must be in the future")
    private LocalDate deadline;
}
