package mh.cyb.root.DpiBatchMeetBackend.modules.event.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.RegistrationDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.mapper.RegistrationMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.repository.EventRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.repository.RegistrationRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @Mock
    private RegistrationRepository registrationRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private RegistrationMapper registrationMapper;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    private User user;
    private User admin;
    private Event event;
    private Registration registration;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");

        admin = new User();
        admin.setId(2L);
        admin.setRoles(new java.util.HashSet<>(Collections.singletonList(Role.ADMINISTRATOR)));

        event = Event.builder()
                .id(1L)
                .title("Test Event")
                .maxAttendees(2)
                .registrationDeadline(LocalDateTime.now().plusDays(1))
                .build();

        registration = Registration.builder()
                .id(1L)
                .event(event)
                .userId(user.getId())
                .status(RegistrationStatus.PENDING)
                .build();
    }

    @Test
    void register_ShouldCreatePending_WhenUnderCapacity() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(registrationRepository.countByEventIdAndStatus(1L, RegistrationStatus.APPROVED)).thenReturn(0);
        when(registrationRepository.findByEventIdAndUserId(1L, user.getId())).thenReturn(Optional.empty());
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);
        when(registrationMapper.toDto(any()))
                .thenReturn(RegistrationDto.builder().status(RegistrationStatus.PENDING).build());

        RegistrationDto result = registrationService.register(1L, user, "Notes");

        assertThat(result.getStatus()).isEqualTo(RegistrationStatus.PENDING);
        verify(registrationRepository).save(any(Registration.class));
    }

    @Test
    void register_ShouldCreateWaitlisted_WhenAtCapacity() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(registrationRepository.countByEventIdAndStatus(1L, RegistrationStatus.APPROVED)).thenReturn(2);
        when(registrationRepository.findByEventIdAndUserId(1L, user.getId())).thenReturn(Optional.empty());

        Registration waitlistedReg = Registration.builder().status(RegistrationStatus.WAITLISTED).build();
        when(registrationRepository.save(any(Registration.class))).thenReturn(waitlistedReg);
        when(registrationMapper.toDto(any()))
                .thenReturn(RegistrationDto.builder().status(RegistrationStatus.WAITLISTED).build());

        RegistrationDto result = registrationService.register(1L, user, "Notes");

        assertThat(result.getStatus()).isEqualTo(RegistrationStatus.WAITLISTED);
    }

    @Test
    void approveRegistration_ShouldSuccess() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);
        when(registrationMapper.toDto(any()))
                .thenReturn(RegistrationDto.builder().status(RegistrationStatus.APPROVED).build());

        RegistrationDto result = registrationService.approveRegistration(1L, admin);

        assertThat(result.getStatus()).isEqualTo(RegistrationStatus.APPROVED);
        assertThat(registration.getStatus()).isEqualTo(RegistrationStatus.APPROVED);
        assertThat(registration.getReviewedBy()).isEqualTo(admin.getId());
    }

    @Test
    void cancelRegistration_ShouldPromoteWaitlisted_WhenApprovedUserCancels() {
        registration.setStatus(RegistrationStatus.APPROVED);
        when(registrationRepository.findByEventIdAndUserId(1L, user.getId())).thenReturn(Optional.of(registration));

        Registration waitlisted = Registration.builder().id(2L).status(RegistrationStatus.WAITLISTED).build();
        when(registrationRepository.findFirstByEventIdAndStatusOrderByRegisteredAtAsc(1L,
                RegistrationStatus.WAITLISTED))
                .thenReturn(Optional.of(waitlisted));

        registrationService.cancelRegistration(1L, user);

        assertThat(registration.getStatus()).isEqualTo(RegistrationStatus.CANCELLED);
        assertThat(waitlisted.getStatus()).isEqualTo(RegistrationStatus.PENDING);
        verify(registrationRepository, times(2)).save(any(Registration.class));
    }

    @Test
    void rejectRegistration_ShouldSetStatusAndReason() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);
        when(registrationMapper.toDto(any()))
                .thenReturn(RegistrationDto.builder().status(RegistrationStatus.REJECTED).build());

        RegistrationDto result = registrationService.rejectRegistration(1L, admin, "Invalid credentials");

        assertThat(result.getStatus()).isEqualTo(RegistrationStatus.REJECTED);
        assertThat(registration.getRejectionReason()).isEqualTo("Invalid credentials");
    }

    @Test
    void getUserRegistrations_ShouldReturnList() {
        when(registrationRepository.findByUserId(user.getId())).thenReturn(java.util.List.of(registration));
        when(registrationMapper.toDto(any())).thenReturn(RegistrationDto.builder().id(1L).build());

        java.util.List<RegistrationDto> result = registrationService.getUserRegistrations(user);

        assertThat(result).hasSize(1);
    }

    @Test
    void getEventRegistrations_ShouldReturnList() {
        when(registrationRepository.findByEventId(event.getId())).thenReturn(java.util.List.of(registration));
        when(registrationMapper.toDto(any())).thenReturn(RegistrationDto.builder().id(1L).build());

        java.util.List<RegistrationDto> result = registrationService.getEventRegistrations(event.getId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getPendingRegistrations_ShouldReturnList() {
        when(registrationRepository.findByEventIdAndStatus(1L, RegistrationStatus.PENDING))
                .thenReturn(java.util.List.of(registration));
        when(registrationMapper.toDto(any())).thenReturn(RegistrationDto.builder().id(1L).build());

        java.util.List<RegistrationDto> result = registrationService.getPendingRegistrations(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void markAsAttended_ShouldSuccess_WhenApproved() {
        registration.setStatus(RegistrationStatus.APPROVED);
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);
        when(registrationMapper.toDto(any()))
                .thenReturn(RegistrationDto.builder().status(RegistrationStatus.ATTENDED).build());

        RegistrationDto result = registrationService.markAsAttended(1L, admin);

        assertThat(result.getStatus()).isEqualTo(RegistrationStatus.ATTENDED);
        assertThat(registration.getStatus()).isEqualTo(RegistrationStatus.ATTENDED);
    }

    @Test
    void markAsAttended_ShouldThrow_WhenNotApproved() {
        registration.setStatus(RegistrationStatus.PENDING);
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));

        assertThatThrownBy(() -> registrationService.markAsAttended(1L, admin))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Only approved registrations");
    }

    @Test
    void isUserRegistered_ShouldReturnTrue_WhenExists() {
        when(registrationRepository.findByEventIdAndUserId(1L, user.getId())).thenReturn(Optional.of(registration));

        boolean result = registrationService.isUserRegistered(1L, user.getId());

        assertThat(result).isTrue();
    }

    @Test
    void getApprovedCount_ShouldReturnCount() {
        when(registrationRepository.countByEventIdAndStatus(1L, RegistrationStatus.APPROVED)).thenReturn(5);

        int count = registrationService.getApprovedCount(1L);

        assertThat(count).isEqualTo(5);
    }

    @Test
    void getApprovedAttendees_ShouldReturnEmptyList() {
        java.util.List<mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileDto> result = registrationService
                .getApprovedAttendees(1L);
        assertThat(result).isEmpty();
    }
}
