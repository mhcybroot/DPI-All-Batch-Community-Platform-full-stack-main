package mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto;

import lombok.Data;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserDto;
import java.time.LocalDate;
import java.util.Set;

@Data
public class ProfileDto {
    private Long id;
    private UserDto user;
    private String bio;
    private LocalDate dateOfBirth;
    // private String title; // Removed as not in Entity
    private String phoneNumber;
    private String linkedInUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String locationCity;
    private String locationCountry;
    private String employmentStatus;
    private Set<SkillDto> skills;
}
