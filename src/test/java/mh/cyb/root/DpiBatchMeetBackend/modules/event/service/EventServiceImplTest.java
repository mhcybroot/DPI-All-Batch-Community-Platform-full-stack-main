package mh.cyb.root.DpiBatchMeetBackend.modules.event.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.Event;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.EventStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.mapper.EventMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.repository.EventRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    private User organizer;
    private User admin;
    private Event event;
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        organizer = new User();
        organizer.setId(1L);
        organizer.setFullName("Organizer User");
        organizer.setRoles(new java.util.HashSet<>(Collections.singletonList(Role.MODERATOR)));

        admin = new User();
        admin.setId(2L);
        admin.setRoles(new java.util.HashSet<>(Collections.singletonList(Role.ADMINISTRATOR)));

        event = Event.builder()
                .id(1L)
                .title("Reunion 2026")
                .eventDate(LocalDateTime.now().plusMonths(1))
                .organizerId(organizer.getId())
                .status(EventStatus.UPCOMING)
                .build();

        eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("Reunion 2026");
    }

    @Test
    void createEvent_ShouldSaveAndReturnDto() {
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle("Reunion 2026");

        when(eventMapper.toEntity(request)).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto result = eventService.createEvent(request, organizer);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Reunion 2026");
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void getEventById_ShouldReturnDto() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto result = eventService.getEventById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getUpcomingEvents_ShouldReturnList() {
        when(eventRepository.findByStatusOrderByEventDateAsc(EventStatus.UPCOMING))
                .thenReturn(List.of(event));
        when(eventMapper.toSummaryDto(event)).thenReturn(new EventSummaryDto());

        List<EventSummaryDto> results = eventService.getUpcomingEvents();

        assertThat(results).hasSize(1);
    }

    @Test
    void updateEvent_ShouldUpdateWhenRequesterIsOrganizer() {
        UpdateEventRequest request = new UpdateEventRequest();
        request.setTitle("Updated Title");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto result = eventService.updateEvent(1L, request, organizer);

        assertThat(result).isNotNull();
        verify(eventMapper).updateEventFromDto(request, event);
        verify(eventRepository).save(event);
    }

    @Test
    void updateEvent_ShouldUpdateWhenRequesterIsAdmin() {
        UpdateEventRequest request = new UpdateEventRequest();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto result = eventService.updateEvent(1L, request, admin);

        assertThat(result).isNotNull();
    }

    @Test
    void updateEvent_ShouldThrowWhenUnauthorized() {
        User otherUser = new User();
        otherUser.setId(3L);
        otherUser.setRoles(new java.util.HashSet<>(Collections.singletonList(Role.MEMBER)));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.updateEvent(1L, new UpdateEventRequest(), otherUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    void deleteEvent_ShouldDeleteWhenAdmin() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        eventService.deleteEvent(1L, admin);

        verify(eventRepository).deleteById(1L);
    }

    @Test
    void deleteEvent_ShouldThrowWhenNotAdmin() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> eventService.deleteEvent(1L, organizer))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Only administrators can delete events");
    }

    @Test
    void updateStatus_ShouldChangeStatus() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        eventService.updateStatus(1L, EventStatus.COMPLETED, admin);

        assertThat(event.getStatus()).isEqualTo(EventStatus.COMPLETED);
        verify(eventRepository).save(event);
    }

    @Test
    void updateStatus_ShouldThrowWhenUnauthorized() {
        User otherUser = new User();
        otherUser.setId(3L);
        otherUser.setRoles(new java.util.HashSet<>(Collections.singletonList(Role.MEMBER)));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.updateStatus(1L, EventStatus.COMPLETED, otherUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    void getEventsByOrganizer_ShouldReturnList() {
        when(eventRepository.findByOrganizerId(1L)).thenReturn(List.of(event));
        when(eventMapper.toSummaryDto(event)).thenReturn(new EventSummaryDto());

        List<EventSummaryDto> results = eventService.getEventsByOrganizer(1L);

        assertThat(results).hasSize(1);
    }
}
