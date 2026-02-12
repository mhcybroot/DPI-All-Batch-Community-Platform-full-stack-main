package mh.cyb.root.DpiBatchMeetBackend.modules.event.mapper;

import mh.cyb.root.DpiBatchMeetBackend.modules.event.domain.Registration;
import mh.cyb.root.DpiBatchMeetBackend.modules.event.dto.RegistrationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class RegistrationMapper {

    @Autowired
    protected mh.cyb.root.DpiBatchMeetBackend.modules.user.repository.UserRepository userRepository;

    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "event.title", target = "eventTitle")
    @Mapping(source = "reviewedBy", target = "reviewedById")
    @Mapping(target = "userName", expression = "java(getUserName(registration.getUserId()))")
    @Mapping(target = "reviewedByName", expression = "java(getUserName(registration.getReviewedBy()))")
    public abstract RegistrationDto toDto(Registration registration);

    protected String getUserName(Long userId) {
        if (userId == null)
            return null;
        return userRepository.findById(userId)
                .map(u -> u.getFullName())
                .orElse("Unknown");
    }
}
