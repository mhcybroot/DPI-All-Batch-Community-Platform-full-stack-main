package mh.cyb.root.DpiBatchMeetBackend.modules.event.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.RegistrationDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.mapper.RegistrationMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.repository.EventRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.repository.RegistrationRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final RegistrationMapper registrationMapper;

    public RegistrationServiceImpl(RegistrationRepository registrationRepository, EventRepository eventRepository,
            RegistrationMapper registrationMapper) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.registrationMapper = registrationMapper;
    }

    @Override
    @Transactional
    public RegistrationDto register(Long eventId, User user, String notes) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getRegistrationDeadline() != null && event.getRegistrationDeadline().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Registration deadline has passed");
        }

        registrationRepository.findByEventIdAndUserId(eventId, user.getId()).ifPresent(r -> {
            if (r.getStatus() != RegistrationStatus.CANCELLED && r.getStatus() != RegistrationStatus.REJECTED) {
                throw new RuntimeException("You are already registered for this event");
            }
            // If already exists but was cancelled/rejected, we'll just update it or
            // delete-and-new.
            // For simplicity, let's just delete the old one if it exists and was
            // cancelled/rejected.
            registrationRepository.delete(r);
        });

        int approvedCount = registrationRepository.countByEventIdAndStatus(eventId, RegistrationStatus.APPROVED);
        RegistrationStatus initialStatus = (event.getMaxAttendees() == null || approvedCount < event.getMaxAttendees())
                ? RegistrationStatus.PENDING
                : RegistrationStatus.WAITLISTED;

        Registration registration = Registration.builder()
                .event(event)
                .userId(user.getId())
                .status(initialStatus)
                .notes(notes)
                .build();

        return registrationMapper.toDto(registrationRepository.save(registration));
    }

    @Override
    @Transactional
    public void cancelRegistration(Long eventId, User user) {
        Registration registration = registrationRepository.findByEventIdAndUserId(eventId, user.getId())
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        RegistrationStatus oldStatus = registration.getStatus();
        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);

        if (oldStatus == RegistrationStatus.APPROVED || oldStatus == RegistrationStatus.PENDING) {
            promoteFromWaitlist(eventId);
        }
    }

    private void promoteFromWaitlist(Long eventId) {
        registrationRepository.findFirstByEventIdAndStatusOrderByRegisteredAtAsc(eventId, RegistrationStatus.WAITLISTED)
                .ifPresent(waitlisted -> {
                    waitlisted.setStatus(RegistrationStatus.PENDING);
                    registrationRepository.save(waitlisted);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationDto> getUserRegistrations(User user) {
        return registrationRepository.findByUserId(user.getId()).stream()
                .map(registrationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationDto> getEventRegistrations(Long eventId) {
        return registrationRepository.findByEventId(eventId).stream()
                .map(registrationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationDto> getPendingRegistrations(Long eventId) {
        return registrationRepository.findByEventIdAndStatus(eventId, RegistrationStatus.PENDING).stream()
                .map(registrationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RegistrationDto approveRegistration(Long registrationId, User admin) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        registration.setStatus(RegistrationStatus.APPROVED);
        registration.setReviewedBy(admin.getId());
        registration.setReviewedAt(LocalDateTime.now());

        return registrationMapper.toDto(registrationRepository.save(registration));
    }

    @Override
    @Transactional
    public RegistrationDto rejectRegistration(Long registrationId, User admin, String reason) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        registration.setStatus(RegistrationStatus.REJECTED);
        registration.setRejectionReason(reason);
        registration.setReviewedBy(admin.getId());
        registration.setReviewedAt(LocalDateTime.now());

        RegistrationDto dto = registrationMapper.toDto(registrationRepository.save(registration));
        promoteFromWaitlist(registration.getEvent().getId());
        return dto;
    }

    @Override
    @Transactional
    public RegistrationDto markAsAttended(Long registrationId, User admin) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        if (registration.getStatus() != RegistrationStatus.APPROVED) {
            throw new RuntimeException("Only approved registrations can be marked as attended");
        }

        registration.setStatus(RegistrationStatus.ATTENDED);
        return registrationMapper.toDto(registrationRepository.save(registration));
    }

    @Override
    public List<ProfileDto> getApprovedAttendees(Long eventId) {
        // This would normally involve joining with profile, but for now we return empty
        // or just placeholders
        // In a real app, we'd fetch profiles by user IDs
        return List.of();
    }

    @Override
    public boolean isUserRegistered(Long eventId, Long userId) {
        return registrationRepository.findByEventIdAndUserId(eventId, userId).isPresent();
    }

    @Override
    public int getApprovedCount(Long eventId) {
        return registrationRepository.countByEventIdAndStatus(eventId, RegistrationStatus.APPROVED);
    }
}
