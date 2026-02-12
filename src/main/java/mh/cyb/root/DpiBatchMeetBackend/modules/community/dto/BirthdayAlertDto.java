package mh.cyb.root.DpiBatchMeetBackend.modules.community.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class BirthdayAlertDto {
    private Long userId;
    private String fullName;
    private Integer age;
}
