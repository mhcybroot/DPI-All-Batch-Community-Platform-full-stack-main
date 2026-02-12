package mh.cyb.root.DpiBatchMeetBackend.modules.event.controller;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.EventStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.service.EventService;
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
import static org.mockito.Mockito.when;

class EventControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private EventController eventController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
    }

    @Test
    void getUpcomingEvents_ShouldReturnOk() {
        when(eventService.getUpcomingEvents()).thenReturn(Collections.emptyList());
        ResponseEntity<List<EventSummaryDto>> response = eventController.getUpcomingEvents();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getEvent_ShouldReturnOk() {
        when(eventService.getEventById(1L)).thenReturn(new EventDto());
        ResponseEntity<EventDto> response = eventController.getEvent(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createEvent_ShouldReturnCreated() {
        CreateEventRequest request = new CreateEventRequest();
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(eventService.createEvent(any(), any())).thenReturn(new EventDto());

        ResponseEntity<EventDto> response = eventController.createEvent(request, userDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void updateEvent_ShouldReturnOk() {
        UpdateEventRequest request = new UpdateEventRequest();
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(eventService.updateEvent(anyLong(), any(), any())).thenReturn(new EventDto());

        ResponseEntity<EventDto> response = eventController.updateEvent(1L, request, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteEvent_ShouldReturnNoContent() {
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));

        ResponseEntity<Void> response = eventController.deleteEvent(1L, userDetails);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void updateStatus_ShouldReturnOk() {
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(eventService.updateStatus(anyLong(), any(), any())).thenReturn(new EventDto());

        ResponseEntity<EventDto> response = eventController.updateStatus(1L, EventStatus.COMPLETED, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getEventsByOrganizer_ShouldReturnOk() {
        when(eventService.getEventsByOrganizer(1L)).thenReturn(Collections.emptyList());
        ResponseEntity<List<EventSummaryDto>> response = eventController.getEventsByOrganizer(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
