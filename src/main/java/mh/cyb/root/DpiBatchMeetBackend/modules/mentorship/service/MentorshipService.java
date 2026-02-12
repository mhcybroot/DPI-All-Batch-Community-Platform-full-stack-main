package mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.domain.ConnectionStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MentorshipService {
    MentorProfileDto registerAsMentor(RegisterMentorRequest request, User user);

    Page<MentorProfileDto> searchMentors(String expertise, Pageable pageable);

    ConnectionRequestDto sendRequest(CreateConnectionRequest request, User mentee);

    List<ConnectionRequestDto> getMyRequests(User user); // As mentee

    List<ConnectionRequestDto> getIncomingRequests(User user); // As mentor

    ConnectionRequestDto updateRequestStatus(Long requestId, ConnectionStatus status, User user);
}
