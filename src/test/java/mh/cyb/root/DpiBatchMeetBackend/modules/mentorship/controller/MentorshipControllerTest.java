package mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.domain.ConnectionStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.domain.MentorStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.mentorship.service.MentorshipService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class MentorshipControllerTest {

    @Mock
    private MentorshipService mentorshipService;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private MentorshipController mentorshipController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    }

    @Test
    void registerMentor_ShouldReturnOk() {
        RegisterMentorRequest request = new RegisterMentorRequest();
        MentorProfileDto dto = MentorProfileDto.builder().build();
        when(mentorshipService.registerAsMentor(any(), any())).thenReturn(dto);

        ResponseEntity<MentorProfileDto> response = mentorshipController.registerMentor(userDetails, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void searchMentors_ShouldReturnOk() {
        when(mentorshipService.searchMentors(any(), any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        ResponseEntity<Page<MentorProfileDto>> response = mentorshipController.searchMentors(null, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void sendRequest_ShouldReturnOk() {
        CreateConnectionRequest request = new CreateConnectionRequest();
        ConnectionRequestDto dto = ConnectionRequestDto.builder().build();
        when(mentorshipService.sendRequest(any(), any())).thenReturn(dto);

        ResponseEntity<ConnectionRequestDto> response = mentorshipController.sendRequest(userDetails, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateStatus_ShouldReturnOk() {
        ConnectionRequestDto dto = ConnectionRequestDto.builder().build();
        when(mentorshipService.updateRequestStatus(eq(1L), eq(ConnectionStatus.ACTIVE), any())).thenReturn(dto);

        ResponseEntity<ConnectionRequestDto> response = mentorshipController.updateStatus(1L, userDetails,
                ConnectionStatus.ACTIVE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getMyRequests_ShouldReturnOk() {
        when(mentorshipService.getMyRequests(any())).thenReturn(Collections.emptyList());

        ResponseEntity<List<ConnectionRequestDto>> response = mentorshipController.getMyRequests(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getIncomingRequests_ShouldReturnOk() {
        when(mentorshipService.getIncomingRequests(any())).thenReturn(Collections.emptyList());

        ResponseEntity<List<ConnectionRequestDto>> response = mentorshipController.getIncomingRequests(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
