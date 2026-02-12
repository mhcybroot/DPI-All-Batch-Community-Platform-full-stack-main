package mh.cyb.root.DpiBatchMeetBackend.modules.event.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.EventStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;

import java.util.List;

public interface EventService {
    List<EventSummaryDto> getUpcomingEvents();

    EventDto getEventById(Long id);

    EventDto createEvent(CreateEventRequest request, User organizer);

    EventDto updateEvent(Long id, UpdateEventRequest request, User requester);

    void deleteEvent(Long id, User requester);

    List<EventSummaryDto> getEventsByOrganizer(Long organizerId);

    EventDto updateStatus(Long id, EventStatus status, User requester);
}
