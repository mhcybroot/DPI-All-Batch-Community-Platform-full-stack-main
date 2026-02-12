package mh.cyb.root.DpiBatchMeetBackend.modules.event.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.CreatePollRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.PollDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.service.PollService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PollControllerTest {

    @Mock
    private PollService pollService;
    @Mock
    private UserService userService;
    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private PollController pollController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
    }

    @Test
    void createPoll_ShouldReturnCreated() {
        CreatePollRequest request = new CreatePollRequest();
        when(userDetails.getUsername()).thenReturn("user@test.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(pollService.createPoll(any(), any())).thenReturn(PollDto.builder().build());

        ResponseEntity<PollDto> response = pollController.createPoll(request, userDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void vote_ShouldReturnOk() {
        when(userDetails.getUsername()).thenReturn("user@test.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        doNothing().when(pollService).vote(anyLong(), anyLong(), any());

        ResponseEntity<Void> response = pollController.vote(1L, 1L, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(pollService).vote(1L, 1L, user);
    }

    @Test
    void closePoll_ShouldReturnOk() {
        when(userDetails.getUsername()).thenReturn("user@test.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        doNothing().when(pollService).closePoll(anyLong(), any());

        ResponseEntity<Void> response = pollController.closePoll(1L, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getPollResults_ShouldReturnOk() {
        when(pollService.getPollResults(anyLong())).thenReturn(PollDto.builder().build());

        ResponseEntity<PollDto> response = pollController.getPollResults(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getActivePolls_ShouldReturnOk() {
        when(pollService.getActivePolls()).thenReturn(Collections.emptyList());

        ResponseEntity<List<PollDto>> response = pollController.getActivePolls();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(pollService).getActivePolls();
    }
}
