package mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.service;

import lombok.RequiredArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.common.exception.ResourceNotFoundException;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.domain.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.repository.MentorProfileRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.repository.MentorshipConnectionRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.dto.UserSummaryDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MentorshipServiceImpl implements MentorshipService {

    private final MentorProfileRepository mentorProfileRepository;
    private final MentorshipConnectionRepository connectionRepository;
    private final UserService userService;

    @Override
    public MentorProfileDto registerAsMentor(RegisterMentorRequest request, User user) {
        Optional<MentorProfile> existing = mentorProfileRepository.findByUser_Id(user.getId());
        MentorProfile profile;
        if (existing.isPresent()) {
            profile = existing.get();
            profile.setExpertise(request.getExpertise());
            profile.setMaxMentees(request.getMaxMentees());
            profile.setStatus(request.getStatus());
        } else {
            profile = MentorProfile.builder()
                    .user(user)
                    .expertise(request.getExpertise())
                    .maxMentees(request.getMaxMentees())
                    .status(request.getStatus())
                    .build();
        }
        return mapToDto(mentorProfileRepository.save(profile));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MentorProfileDto> searchMentors(String expertise, Pageable pageable) {
        if (expertise != null && !expertise.isEmpty()) {
            return mentorProfileRepository
                    .findByExpertiseContainingIgnoreCaseAndStatus(expertise, MentorStatus.OPEN, pageable)
                    .map(this::mapToDto);
        }
        return mentorProfileRepository.findByStatus(MentorStatus.OPEN, pageable).map(this::mapToDto);
    }

    @Override
    public ConnectionRequestDto sendRequest(CreateConnectionRequest request, User mentee) {
        // Validation: Cannot request self
        if (request.getMentorId().equals(mentee.getId())) {
            throw new RuntimeException("Cannot send mentorship request to self");
        }

        // Check if mentor exists
        User mentorUser = userService.getUserById(request.getMentorId());
        // Verify mentor profile
        mentorProfileRepository.findByUser_Id(mentorUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));

        if (connectionRepository.findByMentor_IdAndMentee_IdAndStatusNot(
                request.getMentorId(), mentee.getId(), ConnectionStatus.REJECTED).isPresent()) {
            throw new RuntimeException("Connection already exists or is pending");
        }

        MentorshipConnection connection = MentorshipConnection.builder()
                .mentee(mentee)
                .mentor(mentorUser)
                .requestMessage(request.getRequestMessage())
                .status(ConnectionStatus.PENDING)
                .build();

        return mapToDto(connectionRepository.save(connection));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConnectionRequestDto> getMyRequests(User user) {
        return connectionRepository.findByMentee_Id(user.getId())
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConnectionRequestDto> getIncomingRequests(User user) {
        // Assuming user is a mentor
        return connectionRepository.findByMentor_IdAndStatus(user.getId(), ConnectionStatus.PENDING)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public ConnectionRequestDto updateRequestStatus(Long requestId, ConnectionStatus status, User user) {
        MentorshipConnection connection = connectionRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        // Only mentor can accept/reject
        if (!connection.getMentor().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        connection.setStatus(status);

        // Logic: If Accepted, maybe check max mentees?
        // For now keep simple

        return mapToDto(connectionRepository.save(connection));
    }

    private MentorProfileDto mapToDto(MentorProfile profile) {
        return MentorProfileDto.builder()
                .id(profile.getId())
                .user(mapUser(profile.getUser()))
                .expertise(profile.getExpertise())
                .status(profile.getStatus())
                .maxMentees(profile.getMaxMentees())
                .build();
    }

    private ConnectionRequestDto mapToDto(MentorshipConnection c) {
        return ConnectionRequestDto.builder()
                .id(c.getId())
                .mentee(mapUser(c.getMentee()))
                .mentor(mapUser(c.getMentor()))
                .status(c.getStatus())
                .requestMessage(c.getRequestMessage())
                .build();
    }

    private UserSummaryDto mapUser(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getFullName())
                .build();
    }
}
