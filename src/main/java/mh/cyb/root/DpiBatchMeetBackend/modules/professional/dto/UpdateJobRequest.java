package mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto;

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
public class UpdateJobRequest {
    private String title;
    private String companyName;
    private String location;
    private JobType jobType;
    private String description;
    private String requirements;
    private String applicationLink;
    private LocalDate deadline;
}
