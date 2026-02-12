package mh.cyb.root.DpiBatchMeetBackend.modules.event.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.RegistrationDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;

import java.util.List;

public interface RegistrationService {
    RegistrationDto register(Long eventId, User user, String notes);

    void cancelRegistration(Long eventId, User user);

    List<RegistrationDto> getUserRegistrations(User user);

    List<RegistrationDto> getEventRegistrations(Long eventId);

    List<RegistrationDto> getPendingRegistrations(Long eventId);

    RegistrationDto approveRegistration(Long registrationId, User admin);

    RegistrationDto rejectRegistration(Long registrationId, User admin, String reason);

    RegistrationDto markAsAttended(Long registrationId, User admin);

    List<ProfileDto> getApprovedAttendees(Long eventId);

    boolean isUserRegistered(Long eventId, Long userId);

    int getApprovedCount(Long eventId);
}
