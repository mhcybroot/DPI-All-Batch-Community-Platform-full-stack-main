package mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.domain.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.dto.ConnectionRequestDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.dto.CreateConnectionRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.dto.MentorProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.dto.RegisterMentorRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.repository.MentorProfileRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.repository.MentorshipConnectionRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MentorshipServiceImplTest {

    @Mock
    private MentorProfileRepository mentorProfileRepository;
    @Mock
    private MentorshipConnectionRepository connectionRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    private User user;
    private User mentorUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setFullName("Mentee User");

        mentorUser = new User();
        mentorUser.setId(2L);
        mentorUser.setFullName("Mentor User");
    }

    @Test
    void registerAsMentor_ShouldSaveAndReturnDto() {
        RegisterMentorRequest request = new RegisterMentorRequest();
        request.setExpertise(Collections.singletonList("Java"));
        request.setStatus(MentorStatus.OPEN);
        request.setMaxMentees(5);

        MentorProfile profile = MentorProfile.builder()
                .id(1L)
                .user(user)
                .expertise(request.getExpertise())
                .status(MentorStatus.OPEN)
                .build();

        when(mentorProfileRepository.findByUser_Id(user.getId())).thenReturn(Optional.empty());
        when(mentorProfileRepository.save(any(MentorProfile.class))).thenReturn(profile);

        MentorProfileDto result = mentorshipService.registerAsMentor(request, user);

        assertNotNull(result);
        assertEquals(MentorStatus.OPEN, result.getStatus());
        verify(mentorProfileRepository).save(any(MentorProfile.class));
    }

    @Test
    void sendRequest_ShouldSaveConnection() {
        CreateConnectionRequest request = new CreateConnectionRequest();
        request.setMentorId(2L);
        request.setRequestMessage("Please mentor me");

        when(userService.getUserById(2L)).thenReturn(mentorUser);
        when(mentorProfileRepository.findByUser_Id(2L)).thenReturn(Optional.of(new MentorProfile()));
        when(connectionRepository.findByMentor_IdAndMentee_IdAndStatusNot(any(), any(), any()))
                .thenReturn(Optional.empty());

        MentorshipConnection connection = MentorshipConnection.builder()
                .id(1L)
                .mentee(user)
                .mentor(mentorUser)
                .status(ConnectionStatus.PENDING)
                .build();

        when(connectionRepository.save(any(MentorshipConnection.class))).thenReturn(connection);

        ConnectionRequestDto result = mentorshipService.sendRequest(request, user);

        assertNotNull(result);
        assertEquals(ConnectionStatus.PENDING, result.getStatus());
        verify(connectionRepository).save(any(MentorshipConnection.class));
    }

    @Test
    void searchMentors_ShouldReturnPage() {
        when(mentorProfileRepository.findByStatus(any(), any()))
                .thenReturn(org.springframework.data.domain.Page.empty());

        var result = mentorshipService.searchMentors(null, org.springframework.data.domain.Pageable.unpaged());

        assertNotNull(result);
    }

    @Test
    void updateRequestStatus_ShouldUpdateStatus() {
        MentorshipConnection connection = new MentorshipConnection();
        connection.setId(1L);
        connection.setMentor(mentorUser);
        connection.setMentee(user);
        connection.setStatus(ConnectionStatus.PENDING);

        when(connectionRepository.findById(1L)).thenReturn(Optional.of(connection));
        when(connectionRepository.save(any(MentorshipConnection.class))).thenReturn(connection);

        ConnectionRequestDto result = mentorshipService.updateRequestStatus(1L, ConnectionStatus.ACTIVE, mentorUser);

        assertNotNull(result);
        assertEquals(ConnectionStatus.ACTIVE, result.getStatus());
    }

    @Test
    void getMyRequests_ShouldReturnList() {
        when(connectionRepository.findByMentee_Id(1L)).thenReturn(Collections.emptyList());

        var result = mentorshipService.getMyRequests(user);

        assertNotNull(result);
    }

    @Test
    void getIncomingRequests_ShouldReturnList() {
        when(connectionRepository.findByMentor_IdAndStatus(2L, ConnectionStatus.PENDING))
                .thenReturn(Collections.emptyList());

        var result = mentorshipService.getIncomingRequests(mentorUser);

        assertNotNull(result);
    }
}
