package mh.cyb.root.DpiBatchMeetBackend.modules.event.mapper;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.Event;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.*;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class EventMapper {

    @Autowired
    protected mh.cyb.root.DpiBatchMeetBackend.modules.user.repository.UserRepository userRepository;

    @Autowired
    protected mh.cyb.root.DpiBatchMeetBackend.modules.event.repository.RegistrationRepository registrationRepository;

    @Mapping(target = "organizerName", expression = "java(getOrganizerName(event.getOrganizerId()))")
    @Mapping(target = "currentAttendees", expression = "java(getAttendeeCount(event.getId()))")
    public abstract EventDto toDto(Event event);

    @Mapping(target = "currentAttendees", expression = "java(getAttendeeCount(event.getId()))")
    public abstract EventSummaryDto toSummaryDto(Event event);

    public abstract Event toEntity(CreateEventRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEventFromDto(UpdateEventRequest request, @MappingTarget Event event);

    protected String getOrganizerName(Long userId) {
        if (userId == null)
            return null;
        return userRepository.findById(userId)
                .map(u -> u.getFullName())
                .orElse("Unknown");
    }

    protected int getAttendeeCount(Long eventId) {
        if (eventId == null)
            return 0;
        return registrationRepository.countByEventIdAndStatus(eventId,
                mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.RegistrationStatus.APPROVED);
    }
}
