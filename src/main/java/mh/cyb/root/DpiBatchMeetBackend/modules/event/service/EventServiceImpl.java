package mh.cyb.root.DpiBatchMeetBackend.modules.event.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.Event;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.EventStatus;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.*;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.mapper.EventMapper;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.repository.EventRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public List<EventSummaryDto> getUpcomingEvents() {
        return eventRepository.findByStatusOrderByEventDateAsc(EventStatus.UPCOMING).stream()
                .map(eventMapper::toSummaryDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public EventDto getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return eventMapper.toDto(event);
    }

    @Override
    public EventDto createEvent(CreateEventRequest request, User organizer) {
        Event event = eventMapper.toEntity(request);
        event.setOrganizerId(organizer.getId());
        event.setStatus(EventStatus.UPCOMING);
        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public EventDto updateEvent(Long id, UpdateEventRequest request, User requester) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        boolean isAdmin = requester.getRoles()
                .contains(mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role.ADMINISTRATOR);
        if (!isAdmin && !event.getOrganizerId().equals(requester.getId())) {
            throw new RuntimeException("Unauthorized to update this event");
        }

        eventMapper.updateEventFromDto(request, event);
        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public void deleteEvent(Long id, User requester) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found");
        }

        boolean isAdmin = requester.getRoles()
                .contains(mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role.ADMINISTRATOR);
        if (!isAdmin) {
            throw new RuntimeException("Only administrators can delete events");
        }

        eventRepository.deleteById(id);
    }

    @Override
    public List<EventSummaryDto> getEventsByOrganizer(Long organizerId) {
        return eventRepository.findByOrganizerId(organizerId).stream()
                .map(eventMapper::toSummaryDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public EventDto updateStatus(Long id, EventStatus status, User requester) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        boolean isAdmin = requester.getRoles()
                .contains(mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role.ADMINISTRATOR);
        if (!isAdmin && !event.getOrganizerId().equals(requester.getId())) {
            throw new RuntimeException("Unauthorized to update status");
        }

        event.setStatus(status);
        return eventMapper.toDto(eventRepository.save(event));
    }
}
