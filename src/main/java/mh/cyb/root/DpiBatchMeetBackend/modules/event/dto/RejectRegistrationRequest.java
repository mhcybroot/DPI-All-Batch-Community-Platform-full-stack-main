package mh.cyb.root.DpiBatchMeetBackend.modules.event.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectRegistrationRequest {
    @NotBlank(message = "Rejection reason is required")
    private String reason;
}
